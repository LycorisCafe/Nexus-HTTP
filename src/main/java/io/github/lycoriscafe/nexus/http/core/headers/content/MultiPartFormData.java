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

public final class MultiPartFormData {
    private String name;
    private String fileName;
    private Map<String, String> parameters;
    private Object data;

    public String getName() {
        return name;
    }

    private void setName(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getFileName() {
        return fileName;
    }

    private void setFileName(final String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    private void setParameter(final String key,
                              final String value) {
        if (parameters == null) parameters = new HashMap<>();
        parameters.put(key, value);
    }

    public Object getData() {
        return data;
    }

    private void setData(final Object data) {
        this.data = Objects.requireNonNull(data);
    }

    public static Content process(final long requestId,
                                  final RequestConsumer requestConsumer,
                                  final String boundary,
                                  final Integer contentLength,
                                  final boolean chunked,
                                  final boolean gzipped) throws IOException {
        if (chunked) {
            requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "transfer encoding not supported");
            return null;
        }

        Content content = Content.ReadOperations.process(requestId, requestConsumer, "multipart/form-data", contentLength, false, gzipped);
        if (content == null) return null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[]) content.getData());
        BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
        List<MultiPartFormData> data = new NonDuplicateList<>();
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

            MultiPartFormData multiPartFormData = new MultiPartFormData();
            for (int i = 0; i < segmentData.length; i++) {
                if (i == 0 && !segmentData[i].equals("form-data")) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid form segment");
                    return null;
                }
                if (i != 0) {
                    String[] keyVal = segmentData[i].trim().split("=", 2);
                    switch (keyVal[0].trim()) {
                        case "name" -> multiPartFormData.setName(keyVal[1].trim().replaceAll("\"", ""));
                        case "filename" -> multiPartFormData.setFileName(keyVal[1].trim().replaceAll("\"", ""));
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
