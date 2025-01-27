/*
 * Copyright 2025 Lycoris Café
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lycoriscafe.nexus.http.core.headers.content;

import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq.HttpRequest;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Content type {@code multipart/form-data} for <b>incoming content</b>. If request has header {@code Content-Type: multipart/form-data}, then the
 * {@code Content.getData()} type should cast to {@code List<MultipartFormData>}.
 * <pre>
 *     {@code
 *     // 'request' is from endpoint parameter (HttpPostRequest, ...)
 *     var content = request.getContent();
 *     if (content.getContentType().equals("multipart/form-data")) {
 *         List<MultipartFormData> formDataList = (List<MultipartFormData>) content.getData();
 *         for (MultipartFormData formData : formDataList) {
 *              // ...
 *         }
 *     }
 *     }
 * </pre>
 *
 * @see Content
 * @see Content#getData()
 * @see <a href="https://datatracker.ietf.org/doc/rfc7578">Returning Values from Forms: multipart/form-data (rfc7578)</a>
 * @since v1.0.0
 */
public final class MultipartFormData {
    private static final Logger logger = LoggerFactory.getLogger(MultipartFormData.class);

    private String name;
    private String fileName;
    private String contentType;
    private Map<String, String> parameters;
    private byte[] data;

    /**
     * Get the name of the form-data part.
     *
     * @return Name of the form-data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the form-data part.
     *
     * @param name Name of the form-data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    private void setName(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Get file-name of the form data part. This can be null.
     *
     * @return File-name of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file-name of the form data part.
     *
     * @param fileName File-name of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    private void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the content-type of the form data part. This can be null.
     *
     * @return Content-type of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the content-type of the form data part.
     *
     * @param contentType Content-type of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get other parameters of the form data part.
     *
     * @return Map of other parameters from the data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Set other parameters of the form data part
     *
     * @param key   Parameter name
     * @param value Parameter value
     * @see MultipartFormData
     * @since v1.0.0
     */
    private void addParameter(final String key,
                              final String value) {
        if (parameters == null) parameters = new HashMap<>();
        parameters.put(key, value);
    }

    /**
     * Get content of the form data part.
     *
     * @return Content of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set the content of the form data part.
     *
     * @param data Content of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    private void setData(final byte[] data) {
        this.data = Objects.requireNonNull(data);
    }

    /**
     * Process incoming {@code multipart/form-data} content type request.
     *
     * @param requestId       {@code HttpRequest} id
     * @param requestConsumer {@code RequestConsumer} bound to the {@code HttpRequest}
     * @param boundary        Form data boundary
     * @param contentLength   {@code Content-Length}
     * @param chunked         {@code Transfer-Encoding} chunked?
     * @param gzipped         {@code Content-Encoding} gzipped?
     * @return New instance of {@code Content}
     * @throws IOException Error while reading data from the socket input stream
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see HttpRequest HttpRequest
     * @see RequestConsumer
     * @see Content
     * @since v1.0.0
     */
    public static Content process(final long requestId,
                                  final RequestConsumer requestConsumer,
                                  final String boundary,
                                  final Integer contentLength,
                                  final boolean chunked,
                                  final boolean gzipped) throws IOException {
        if (chunked) {
            requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "transfer encoding not supported for multipart/form-data", logger);
            return null;
        }

        Content content = Content.ReadOperations.process(requestId, requestConsumer, "multipart/form-data", contentLength, false, gzipped);
        if (content == null) return null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[]) content.getData());
        List<MultipartFormData> formData = new NonDuplicateList<>();

        // remove the first boundary
        var firstBoundary = readLine(inputStream, boundary.getBytes(StandardCharsets.UTF_8));
        if (firstBoundary == null || firstBoundary.size() != 0) {
            System.out.println("1");
            invalidFormSegment(requestConsumer, requestId);
            return null;
        }

        MainLoop:
        while (true) {
            MultipartFormData data = null;

            // check if it's the end of form data
            var choiceLine = readLine(inputStream, null);
            if (choiceLine == null) {
                System.out.println("2");
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            switch (choiceLine.toString(StandardCharsets.UTF_8)) {
                case "--" -> {break MainLoop;}
                case "\r\n" -> {}
                default -> {
                    System.out.println("3");
                    invalidFormSegment(requestConsumer, requestId);
                    return null;
                }
            }

            // read content disposition
            var contentDisposition = readLine(inputStream, "\r\n".getBytes(StandardCharsets.UTF_8));
            if (contentDisposition == null) {
                System.out.println("4");
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            String[] contentDispositionParts = contentDisposition.toString(StandardCharsets.UTF_8).split(":", 2);
            if (!contentDispositionParts[0].equalsIgnoreCase("content-disposition") || contentDispositionParts.length != 2) {
                System.out.println("5");
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            String[] formMetadata = contentDispositionParts[1].split(";", 0);
            if (!formMetadata[0].trim().equalsIgnoreCase("form-data") || formMetadata.length < 2) {
                System.out.println("6");
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            for (int i = 1; i < formMetadata.length; i++) {
                String[] dataParts = formMetadata[i].split("=", 2);
                if (dataParts.length != 2) {
                    System.out.println("7");
                    invalidFormSegment(requestConsumer, requestId);
                    return null;
                }
                switch (dataParts[0].trim()) {
                    case "name" -> {
                        data = new MultipartFormData();
                        data.setName(dataParts[1].trim().replaceAll("\"", ""));
                    }
                    case "filename" -> {
                        if (data == null) {
                            System.out.println("8");
                            invalidFormSegment(requestConsumer, requestId);
                            return null;
                        }
                        data.setFileName(dataParts[1].trim().replaceAll("\"", ""));

                        // read content type
                        var contentType = readLine(inputStream, "\r\n".getBytes(StandardCharsets.UTF_8));
                        if (contentType == null) {
                            System.out.println("9");
                            invalidFormSegment(requestConsumer, requestId);
                            return null;
                        }
                        String[] contentTypeParts = contentType.toString(StandardCharsets.UTF_8).split(":", 2);
                        if (!contentTypeParts[0].equalsIgnoreCase("content-type") || contentTypeParts.length != 2) {
                            System.out.println("10");
                            invalidFormSegment(requestConsumer, requestId);
                            return null;
                        }
                        data.setContentType(contentTypeParts[1].trim());
                    }
                    default -> {
                        if (data == null) {
                            System.out.println("11");
                            invalidFormSegment(requestConsumer, requestId);
                            return null;
                        }
                        data.addParameter(dataParts[0].trim(), dataParts[1].trim().replaceAll("\"", ""));
                    }
                }
            }

            // read empty line between form fields and data
            var emptyLine = readLine(inputStream, "\r\n".getBytes(StandardCharsets.UTF_8));
            if (emptyLine == null || emptyLine.size() != 0) {
                System.out.println("12");
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }

            // read form data
            var contentData = readLine(inputStream, ("\r\n" + boundary).getBytes(StandardCharsets.UTF_8));
            if (contentData == null) {
                System.out.println("13");
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            data.setData(contentData.toByteArray());

            formData.add(data);
        }

        return new Content("multipart/form-data", formData);
    }

    /**
     * Send {@code 400 Bad Request} if the form data ins invalid.
     *
     * @param requestConsumer {@code RequestConsumer} bound to the HTTP request
     * @param requestId       Request id bound to the HTTP request
     * @see #process(long, RequestConsumer, String, Integer, boolean, boolean)
     * @since v1.0.0
     */
    private static void invalidFormSegment(final RequestConsumer requestConsumer,
                                           final long requestId) {
        requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "Invalid form segment", logger);
    }

    /**
     * Read line of bytes into a {@code ByteArrayOutputStream} and return. The end of the line will be the delimiter.
     *
     * @param inputStream Data source
     * @param delimiter Point that should terminate the reading bytes
     * @return If completed a line of data, instance of {@code ByteArrayInputStream}. Else, null.
     * @throws IOException Error while reading data from the input stream
     * @see RequestConsumer
     * @see MultipartFormData
     * @since v1.0.0
     */
    private static ByteArrayOutputStream readLine(final InputStream inputStream,
                                                  final byte[] delimiter) throws IOException {
        if (delimiter == null) {
            var tempByteArray = new ByteArrayOutputStream();
            tempByteArray.write(inputStream.readNBytes(2));
            return tempByteArray;
        }

        byte[] terminatePoint = new byte[delimiter.length];
        var byteArrayOutputStream = new ByteArrayOutputStream();

        int c = inputStream.read(terminatePoint, 0, delimiter.length);
        if (c != delimiter.length) return null;

        while (!Arrays.equals(delimiter, terminatePoint)) {
            int b = inputStream.read();
            if (b == -1) return null;

            byteArrayOutputStream.write(terminatePoint[0]);
            for (int i = 0; i < terminatePoint.length; i++) {
                if (i != terminatePoint.length - 1) terminatePoint[i] = terminatePoint[i + 1];
            }
            terminatePoint[terminatePoint.length - 1] = (byte) b;
        }

        return byteArrayOutputStream;
    }
}
