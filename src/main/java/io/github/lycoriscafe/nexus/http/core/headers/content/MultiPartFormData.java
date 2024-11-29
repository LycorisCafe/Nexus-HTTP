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

import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.util.List;
import java.util.Map;

public class MultiPartFormData {
    String contentDisposition;
    String name;
    String fileName;
    Map<String, String> parameters;
    Object data;

    public MultiPartFormData(String contentDisposition,
                             String name,
                             String fileName,
                             Map<String, String> parameters,
                             Object data) {
        this.contentDisposition = contentDisposition;
        this.name = name;
        this.fileName = fileName;
        this.parameters = parameters;
        this.data = data;
    }

    public String getContentDisposition() {
        return contentDisposition;
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
                                  final List<TransferEncoding> transferEncoding,
                                  final List<ContentEncoding> contentEncoding,
                                  final int contentLength,
                                  final String boundary) {
        return null;
    }
}
