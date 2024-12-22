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
    private void setParameter(final String key,
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
            requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "transfer encoding not supported for multipart/form-data");
            return null;
        }

        Content content = Content.ReadOperations.process(requestId, requestConsumer, "multipart/form-data", contentLength, false, gzipped);
        if (content == null) return null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[]) content.getData());
        List<MultipartFormData> data = new NonDuplicateList<>();
        while (true) {
            String line;
            if ((line = readLine(inputStream)) == null) return invalidFormSegment(requestConsumer, requestId);

            if (line.equals(boundary + "--")) break;
            if (!line.equals(boundary)) return invalidFormSegment(requestConsumer, requestId);

            if ((line = readLine(inputStream)) == null) return invalidFormSegment(requestConsumer, requestId);
            String[] segmentData = line.split(":", 2)[1].trim().split(";", 0);
            if (segmentData.length < 2) {
                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
                return null;
            }

            MultipartFormData multiPartFormData = new MultipartFormData();
            for (int i = 0; i < segmentData.length; i++) {
                if (i == 0 && !segmentData[i].equals("form-data")) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
                    return null;
                }
                if (i != 0) {
                    String[] keyVal = segmentData[i].trim().split("=", 2);
                    switch (keyVal[0].trim()) {
                        case "name" -> multiPartFormData.setName(keyVal[1].trim().replaceAll("\"", ""));
                        case "filename" -> {
                            multiPartFormData.setFileName(keyVal[1].trim().replaceAll("\"", ""));
                            if ((line = readLine(inputStream)) == null) return invalidFormSegment(requestConsumer, requestId);
                            String[] contentType = line.split(":", 0);
                            if (contentType.length > 2 || contentType.length == 0 || !contentType[0].equalsIgnoreCase("content-type")) {
                                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
                                return null;
                            }
                            multiPartFormData.setContentType(contentType[1].trim());
                        }
                        default -> multiPartFormData.setParameter(keyVal[0].trim(), keyVal[1].trim().replaceAll("\"", ""));
                    }
                }
            }

            if ((line = readLine(inputStream)) == null || line.isEmpty()) return invalidFormSegment(requestConsumer, requestId);

            ByteArrayOutputStream byteArrayOutputStream = readByte(inputStream);
            if (byteArrayOutputStream == null) return invalidFormSegment(requestConsumer, requestId);
            multiPartFormData.setData(byteArrayOutputStream.toByteArray());
        }

        return new Content("multipart/form-data", data);
    }

    /**
     * Send {@code 400 Bad Request} if the form data ins invalid.
     *
     * @param requestConsumer {@code RequestConsumer} bound to the HTTP request
     * @param requestId       Request id bound to the HTTP request
     * @return <b>Always return null</b>
     * @see #process(long, RequestConsumer, String, Integer, boolean, boolean)
     * @since v1.0.0
     */
    private static Content invalidFormSegment(final RequestConsumer requestConsumer,
                                              final long requestId) {
        requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
        return null;
    }

    /**
     * Read string line from the given input stream. The line terminator always will {@code \r\n}.
     *
     * @param inputStream {@code InputStream} that data should be read from
     * @return String (charset UTF-8)
     * @throws IOException Error while reading data from the input stream
     * @see #process(long, RequestConsumer, String, Integer, boolean, boolean)
     * @since v1.0.0
     */
    private static String readLine(final InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = readByte(inputStream);
        return byteArrayOutputStream == null ? null :
                byteArrayOutputStream.size() == 0 ? "" : byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    /**
     * Read byte line from the given input stream. The line terminator always will {@code \r\n}.
     *
     * @param inputStream {@code InputStream} that data should be read from
     * @return {@code ByteArrayOutputStream} that can easily convert to {@code String} or {@code byte[]}
     * @throws IOException Error while reading data from the input stream
     * @see #process(long, RequestConsumer, String, Integer, boolean, boolean)
     * @since v1.0.0
     */
    private static ByteArrayOutputStream readByte(final InputStream inputStream) throws IOException {
        byte[] lineTerminator = "\r\n".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] terminatePoint = new byte[2];

        int c = inputStream.read(terminatePoint, 0, 2);
        if (c != 2) return null;

        while (!Arrays.equals(lineTerminator, terminatePoint)) {
            int b = inputStream.read();
            if (b == -1) return null;
            byteArrayOutputStream.write(terminatePoint[0]);
            terminatePoint[0] = terminatePoint[1];
            terminatePoint[1] = (byte) b;
        }
        return byteArrayOutputStream;
    }
}
