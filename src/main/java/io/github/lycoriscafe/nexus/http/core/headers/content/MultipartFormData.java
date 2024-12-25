/*
 * Copyright 2024 Lycoris Cafe
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
     * Get name of the form-data part.
     *
     * @return Name of the form-data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the form-data part.
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
     * Set file-name of the form data part.
     *
     * @param fileName File-name of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    private void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get content-type of the form data part. This can be null.
     *
     * @return Content-type of the form data part
     * @see MultipartFormData
     * @since v1.0.0
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set content-type of the form data part.
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
     * Set content of the form data part.
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
     * @throws IOException Error while reading data from socket input stream
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
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
        List<MultipartFormData> data = new NonDuplicateList<>();
        while (true) {
            MultipartFormData formData = new MultipartFormData();

            var readBoundary = readLine(inputStream);
            if (readBoundary == null || readBoundary.size() == 0) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            if (readBoundary.toString(StandardCharsets.UTF_8).equals(boundary + "--")) break;
            if (!readBoundary.toString(StandardCharsets.UTF_8).equals(boundary)) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }

            var readContentDisposition = readLine(inputStream);
            if (readContentDisposition == null || readContentDisposition.size() == 0) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            String[] contentDispositionHeader = readContentDisposition.toString(StandardCharsets.UTF_8).split(":", 2);
            if (contentDispositionHeader.length != 2 || !contentDispositionHeader[0].equalsIgnoreCase("content-disposition")) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            String[] parameters = contentDispositionHeader[1].split(";", 0);
            if (parameters.length < 2 || !parameters[0].trim().equalsIgnoreCase("form-data")) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            for (int i = 1; i < parameters.length; i++) {
                String[] keyValue = parameters[i].trim().split("=", 2);
                switch (keyValue[0]) {
                    case "form-data" -> {}
                    case "name" -> formData.setName(keyValue[1].replaceAll("\"", ""));
                    case "filename" -> {
                        formData.setFileName(keyValue[1].replaceAll("\"", ""));
                        var readContentType = readLine(inputStream);
                        if (readContentType == null || readContentType.size() == 0) {
                            invalidFormSegment(requestConsumer, requestId);
                            return null;
                        }
                        String[] contentTypeHeader = readContentType.toString(StandardCharsets.UTF_8).split(":", 2);
                        if (contentTypeHeader.length != 2 || !contentTypeHeader[0].equalsIgnoreCase("content-type")) {
                            invalidFormSegment(requestConsumer, requestId);
                            return null;
                        }
                        formData.setContentType(contentTypeHeader[1].trim());
                    }
                    default -> formData.addParameter(keyValue[0], keyValue[1].replaceAll("\"", ""));
                }
            }

            var readEmptyLine = readLine(inputStream);
            if (readEmptyLine == null || readEmptyLine.size() != 0) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }

            var readData = readLine(inputStream);
            if (readData == null) {
                invalidFormSegment(requestConsumer, requestId);
                return null;
            }
            formData.setData(readData.toByteArray());

            data.add(formData);
        }

        return new Content("multipart/form-data", data);
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

    private static ByteArrayOutputStream readLine(final InputStream inputStream) throws IOException {
        byte[] terminatePoint = new byte[RequestConsumer.LINE_TERMINATOR.length];
        var byteArrayOutputStream = new ByteArrayOutputStream();

        int c = inputStream.read(terminatePoint, 0, RequestConsumer.LINE_TERMINATOR.length);
        if (c != RequestConsumer.LINE_TERMINATOR.length) return null;

        while (!Arrays.equals(RequestConsumer.LINE_TERMINATOR, terminatePoint)) {
            int b = inputStream.read();
            if (b == -1) return null;
            byteArrayOutputStream.write(terminatePoint[0]);
            terminatePoint[0] = terminatePoint[1];
            terminatePoint[1] = (byte) b;
        }

        return byteArrayOutputStream;
    }
}
