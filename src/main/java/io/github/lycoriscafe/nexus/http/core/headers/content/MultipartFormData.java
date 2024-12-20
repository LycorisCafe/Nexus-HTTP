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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Content type <code>multipart/form-data</code> for <b>incoming content</b>. If request has header <code>Content-Type: multipart/form-data</code>,
 * then the <code>Content.getData()</code> type should cast to {@code List<MultipartFormData>}.
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
     * Process incoming <code>multipart/form-data</code> content type request.
     *
     * @param requestId       <code>HttpRequest</code> id
     * @param requestConsumer <code>RequestConsumer</code> bound to the <code>HttpRequest</code>
     * @param boundary        Form data boundary
     * @param contentLength   <code>Content-Length</code>
     * @param chunked         <code>Transfer-Encoding</code> chunked?
     * @param gzipped         <code>Content-Encoding</code> gzipped?
     * @return New instance of <code>Content</code>
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
        BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
        List<MultipartFormData> data = new NonDuplicateList<>();
        while (true) {
            String line = bufferedInputStream.readLine();
            if (line.equals(boundary + "--")) break;
            if (!line.equals(boundary)) {
                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid boundary");
                return null;
            }

            String[] segmentData = bufferedInputStream.readLine().split(":", 2)[1].trim().split(";", 0);
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
                            String[] contentType = bufferedInputStream.readLine().split(":", 0);
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

            if (!bufferedInputStream.readLine().isEmpty()) {
                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
                return null;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2];
            byte[] terminator = "\r\n".getBytes(StandardCharsets.UTF_8);

            if (inputStream.read(buffer) != 2) {
                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
                return null;
            }

            while (buffer != terminator) {
                byteArrayOutputStream.write(buffer[0]);
                buffer[0] = buffer[1];
                buffer[1] = (byte) inputStream.read();
            }

            multiPartFormData.setData(byteArrayOutputStream.toByteArray());
        }

        return new Content("multipart/form-data", data);
    }
}
