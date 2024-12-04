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

import java.util.HashMap;
import java.util.Map;

public class Header {
    private final Map<String, String> headers;

    public Header() {
        headers = new HashMap<>();
    }

    public Header addHeader(final String name,
                            final String value) {
        if (name == null || value == null) {
            throw new NullPointerException("name and value cannot be null");
        }
        headers.put(name, value);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static Header processIncomingHeader(final String[] headerParts) {
        return new Header().addHeader(headerParts[0].trim(), headerParts[1].trim());
    }

    public static String processOutgoingHeader(final Header header) {
        if (header == null) return "";

        StringBuilder output = new StringBuilder();
        for (String key : header.getHeaders().keySet()) {
            output.append(key).append(": ").append(header.getHeaders().get(key)).append("\r\n");
        }
        return output.toString();
    }
}
