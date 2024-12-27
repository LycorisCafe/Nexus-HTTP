/*
 * Copyright 2024 Lycoris Café
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

import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.util.Objects;

/**
 * When a client requests to generate a {@code Bearer} access token to a resource, instance of this class will be returned from the target endpoint. The
 * endpoint must be annotated with {@code @BearerEndpoint}.
 * <pre>
 *     {@code
 *     // Example code
 *     var tokenResponse = new BearerTokenResponse("abcdefg1234567")
 *          .setRefreshToken("xyzxyz123098")
 *          .setExpiresIn(3600L);
 *     }
 * </pre>
 *
 * @see BearerEndpoint
 * @see <a href="https://datatracker.ietf.org/doc/rfc6750">The OAuth 2.0 Authorization Framework: Bearer Token Usage (rfc6750)</a>
 * @since v1.0.0
 */
public final class BearerTokenResponse {
    private String bearerToken;
    private Long expiresIn;
    private String refreshToken;
    private String scope;

    /**
     * Get provided access token.
     *
     * @return Access token
     * @see #setBearerToken(String)
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public String getBearerToken() {
        return bearerToken;
    }

    /**
     * Set bearer token.
     *
     * @param bearerToken Generated bearer token
     * @return Same {@code BearerTokenResponse} instance
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public BearerTokenResponse setBearerToken(final String bearerToken) {
        this.bearerToken = Objects.requireNonNull(bearerToken);
        return this;
    }

    /**
     * Get provided token expire time.
     *
     * @return Token expire time
     * @see #setExpiresIn(long)
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Set token expires time
     *
     * @param expiresIn Expire time in seconds.
     * @return Same {@code BearerTokenResponse} instance
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public BearerTokenResponse setExpiresIn(final long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    /**
     * Get provided refresh token.
     *
     * @return Refresh token
     * @see #setRefreshToken(String)
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Set refresh token
     *
     * @param refreshToken Refresh token
     * @return Same {@code BearerTokenResponse} instance
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public BearerTokenResponse setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * Get provided token scope
     *
     * @return Scope of the token
     * @see #setScope(String)
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set the scope of the token
     *
     * @param scope Scope of the token
     * @return Same {@code BearerTokenResponse} instance
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public BearerTokenResponse setScope(final String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Process token response as {@code HttpResponse}
     *
     * @param response        {@code BearerTokenResponse} that should be parsed
     * @param requestId       Target client's request id
     * @param requestConsumer Target client's {@code RequestConsumer}
     * @return New instance of {@code HttpResponse} with processed content
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public static HttpResponse parse(final BearerTokenResponse response,
                                     final long requestId,
                                     final RequestConsumer requestConsumer) {
        return new HttpResponse(requestId, requestConsumer)
                .setCashControl(new CacheControl().setNoStore(true))
                .setContent(new Content("application/json", "{\"access_token\":\"" + response.getBearerToken() + "\"," +
                        "\"token_type\":\"Bearer\"" + ((response.getExpiresIn() != null) ? ",\"expires_in\":" + response.getExpiresIn() : "") +
                        (response.getRefreshToken() != null ? ",\"refresh_token\":\"" + response.getRefreshToken() + "\"" : "") +
                        (response.getScope() != null ? ",\"scope\":\"" + response.getScope() + "\"" : "") + "}"));
    }
}
