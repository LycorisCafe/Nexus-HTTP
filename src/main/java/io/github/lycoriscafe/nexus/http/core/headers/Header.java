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

import java.util.ArrayList;
import java.util.List;

public final class Header {
    private final String name;
    private final List<String> values;
    private final List<String> tokens;

    private Header(final HeaderBuilder builder) {
        name = builder.name;
        values = builder.values;
        tokens = builder.tokens;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public HeaderBuilder builder(final String name) {
        return new HeaderBuilder(name);
    }

    public static class HeaderBuilder {
        private final String name;
        private List<String> values;
        private List<String> tokens;

        private HeaderBuilder(final String name) {
            this.name = name;
        }

        public HeaderBuilder addValue(final String value) {
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(value);
            return this;
        }

        public HeaderBuilder addToken(final String token) {
            if (tokens == null) {
                tokens = new ArrayList<>();
            }
            tokens.add(token);
            return this;
        }

        public Header build() {
            return new Header(this);
        }
    }
}
