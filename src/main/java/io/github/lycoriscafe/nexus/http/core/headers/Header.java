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

package io.github.lycoriscafe.nexus.http.core.headers;

import java.util.List;

public class Header {
    private final String name;
    private final String value;

    public Header(final String name,
                  final String value) {
        if (name == null || value == null) {
            throw new NullPointerException("name and value cannot be null");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static Header parseIncomingHeader(final String[] headerParts) {
        return new Header(headerParts[0].trim(), headerParts[1].trim());
    }

    public static String parseOutgoingHeaders(final List<Header> headers) {
        if (headers == null || headers.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        for (Header header : headers) {
            output.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
        }
        return output.toString();
    }
}
