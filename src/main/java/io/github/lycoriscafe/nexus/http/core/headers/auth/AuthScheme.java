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

package io.github.lycoriscafe.nexus.http.core.headers.auth;

/**
 * Supported authentication schemes by the server.
 *
 * @since v1.0.0
 */
public enum AuthScheme {
    /**
     * Basic authentication scheme.
     *
     * @see AuthScheme
     * @see io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.basic
     * @since v1.0.0
     */
    BASIC("Basic"),
    /**
     * Bearer authentication scheme.
     *
     * @see AuthScheme
     * @see io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer
     * @since v1.0.0
     */
    BEARER("Bearer");
//    Concealed("Concealed"),
//    Digest("Digest"),
//    DPoP("DPoP"),
//    GNAP("GNAP"),
//    HOBA("HOBA"),
//    Mutual("Mutual"),
//    Negotiate("Negotiate"),
//    OAuth("OAuth"),
//    PrivateToken("PrivateToken"),
//    SCRAM_SHA_1("SCRAM-SHA-1"),
//    SCRAM_SHA_256("SCRAM-SHA-256"),
//    vapid("vapid")

    private final String value;

    /**
     * Auth Scheme.
     *
     * @param value String value to set in header
     * @see AuthScheme
     * @since v1.0.0
     */
    AuthScheme(String value) {
        this.value = value;
    }

    /**
     * Get the target 'AuthScheme' value to set in header.
     *
     * @return 'AuthScheme' value to set in header
     * @see AuthScheme
     * @since v1.0.0
     */
    public String getValue() {
        return value;
    }
}
