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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class MultiPartFormData {
    private String name;
    private String fileName;
    private Map<String, String> parameters;
    private Object data;

    private MultiPartFormData() {
    }

    public MultiPartFormData(final String name,
                             final String fileName,
                             final Map<String, String> parameters,
                             final Object data) {
        this.name = name;
        this.fileName = fileName;
        this.parameters = parameters;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Object getData() {
        return data;
    }


    public static Content process(final RequestConsumer requestConsumer,
                                  final HashSet<TransferEncoding> transferEncoding,
                                  final HashSet<ContentEncoding> contentEncoding,
                                  final int contentLength,
                                  final String boundary) {
        if (transferEncoding != null && transferEncoding.contains(TransferEncoding.CHUNKED)) {
            requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
            return null;
        }

        Content content = Content.processCommonContentType(requestConsumer, transferEncoding, contentEncoding,
                contentLength, "multipart/form-data");
        if (content == null) return null;

        List<MultiPartFormData> data = new ArrayList<>();
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(((ByteArrayOutputStream) content.getData()).toByteArray());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        int boundaryLength = boundary.length();
        int len = 0;
        try {
            while (true) {
                MultiPartFormData multiPartFormData = new MultiPartFormData();

                String b = bufferedReader.readLine();
                if (!b.equals(boundary)) {
                    if (b.equals(boundary + "--")) {
                        len += 2;
                        if (contentLength != len) {
                            requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                            return null;
                        }
                        break;
                    }
                }
                len += boundaryLength;

                String[] dispositionHeader = bufferedReader.readLine().split(":", 0);
                if (!(dispositionHeader.length > 1) ||
                        !dispositionHeader[0].equalsIgnoreCase("content-disposition")) {
                    requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }

                String[] params = dispositionHeader[1].split(";", 0);
                if (!(params.length > 2) ||
                        !params[1].trim().equalsIgnoreCase("form-data") ||
                        !params[2].trim().startsWith("name=")) {
                    requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }

                for (String param : params) {
                    String[] keyVal = param.trim().split("=", 0);
                    if (keyVal[0].equalsIgnoreCase("form-data")) continue;
                    if (keyVal[0].equalsIgnoreCase("name")) {
                        multiPartFormData.name = keyVal[1].replaceAll("\"", "");
                        continue;
                    }
                    if (keyVal[0].equalsIgnoreCase("file-name")) {
                        multiPartFormData.fileName = keyVal[1].replaceAll("\"", "");
                        continue;
                    }
                    multiPartFormData.parameters.put(keyVal[0], keyVal[1]);
                }

                List<Byte> termination = new ArrayList<>();
                List<Byte> marker = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    termination.add((byte) '\r');
                    termination.add((byte) '\n');
                    marker.add((byte) '\r');
                    marker.add((byte) '\n');
                }

                int n;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while ((n = byteArrayInputStream.read()) != -1) {
                    len++;
                    if (len > contentLength) {
                        requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                        return null;
                    }

                    termination.removeFirst();

                    if (n == '\r' || n == '\n') {
                        termination.add((byte) n);
                        if (termination.equals(marker)) break;
                    }

                    byteArrayOutputStream.write(n);
                }

                if (multiPartFormData.fileName == null) {
                    multiPartFormData.data = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
                } else {
                    multiPartFormData.data = byteArrayOutputStream;
                }

                data.add(multiPartFormData);
            }
        } catch (Exception e) {
            requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
            return null;
        }

        content.data = data;

        return content;
    }
}
