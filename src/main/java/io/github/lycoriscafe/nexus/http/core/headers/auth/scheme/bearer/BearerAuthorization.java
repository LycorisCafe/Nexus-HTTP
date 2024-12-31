/*
 * Copyright 2025 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.core.headers.auth.AuthScheme;
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization;

/**
 * The {@code Bearer} authorization for HTTP. An instance of this class will receive to the server endpoint when the client provided the
 * {@code Authorization} header with authentication scheme {@code Bearer}. Only authorization header field will be treated as a proper authorization
 * (see rfc6750 - 2).
 * <pre>
 *     {@code
 *     <!-- General header format -->
 *     Authorization: Bearer BearerAccessToken
 *     }
 * </pre>
 *
 * @see Authorization
 * @see <a href="https://datatracker.ietf.org/doc/rfc6750">The OAuth 2.0 Authorization Framework: Bearer Token Usage (rfc6750)</a>
 * @since v1.0.0
 */
public final class BearerAuthorization extends Authorization {
    private final String accessToken;

    /**
     * Create an instance of {@code BearerAuthorization}.
     *
     * @param accessToken Received access token
     * @see BearerAuthorization
     * @since v1.0.0
     */
    public BearerAuthorization(final String accessToken) {
        super(AuthScheme.BEARER);
        this.accessToken = accessToken;
    }

    /**
     * Get received access token
     *
     * @return Received access token
     * @see BearerAuthorization
     * @since v1.0.0
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Process provided string to a new instance of {@code BearerAuthorization}
     *
     * @param params Bearer authorization header value part (only data)
     * @return New instance of {@code BearerAuthorization}
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BearerAuthorization
     * @since v1.0.0
     */
    public static Authorization processIncomingAuth(final String params) {
        return new BearerAuthorization(params);
    }
}
