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

public final class Header {
    private final String name;
    private final String value;

    private Header(final HeaderBuilder builder) {
        name = builder.name;
        value = builder.value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static HeaderBuilder builder(final String name,
                                        final String value) {
        return new HeaderBuilder(name, value);
    }

    public static Header processIncomingHeader(String[] headerParts) {
        return builder(headerParts[0].trim(), headerParts[1].trim()).build();
    }

    public static class HeaderBuilder {
        private final String name;
        private final String value;

        private HeaderBuilder(final String name,
                              final String value) {
            this.name = name;
            this.value = value;
        }

        public Header build() {
            return new Header(this);
        }
    }
}