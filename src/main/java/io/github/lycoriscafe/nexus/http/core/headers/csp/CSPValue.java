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

package io.github.lycoriscafe.nexus.http.core.headers.csp;

public enum CSPValue {
    NONE("'none'"),
    SELF("'self'"),
    STRICT_DYNAMIC("'strict-dynamic'"),
    REPORT_SAMPLE("'report-sample'"),
    INLINE_SPECULATION_RULES("'inline-speculation-rules'"),
    UNSAFE_INLINE("'unsafe-inline'"),
    UNSAFE_EVAL("'unsafe-eval'"),
    UNSAFE_HASHES("'unsafe-hashes'"),
    WASM_UNSAFE_EVAL("'wasm-unsafe-eval'");

    private final String value;

    CSPValue(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
