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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

/**
 * When client requests to generate a <code>Bearer</code> access token to a resource, instance of this class will be returned from the target
 * endpoint. The endpoint must be annotated with <code>@BearerEndpoint</code>.
 *
 * @see BearerEndpoint
 * @see <a href="https://datatracker.ietf.org/doc/rfc6750">The OAuth 2.0 Authorization Framework: Bearer Token Usage (rfc6750)</a>
 * @since v1.0.0
 */
public final class BearerTokenResponse {
    private final String bearerToken;
    private long expiresIn = -1L;
    private String refreshToken;
    private String scope;

    /**
     * Create and instance of <code>BearerTokenResponse</code>.
     *
     * @param bearerToken Generated bearer token
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public BearerTokenResponse(final String bearerToken) {
        this.bearerToken = bearerToken;
    }

    /**
     * Get provided access token.
     *
     * @return Access token
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public String getBearerToken() {
        return bearerToken;
    }

    /**
     * Get provided token expire time.
     *
     * @return Token expire time
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Set token expire time
     *
     * @param expiresIn Expire time in seconds.
     * @return Same <code>BearerTokenResponse</code> instance
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
     * @return Same <code>BearerTokenResponse</code> instance
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
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set scope of the token
     *
     * @param scope Scope of the token
     * @return Same <code>BearerTokenResponse</code> instance
     * @see BearerTokenResponse
     * @since v1.0.0
     */
    public BearerTokenResponse setScope(final String scope) {
        this.scope = scope;
        return this;
    }

    public static HttpResponse parse(final BearerTokenResponse response,
                                     final long requestId,
                                     final RequestConsumer requestConsumer) {
        return new HttpResponse(requestId, requestConsumer, HttpStatusCode.OK).setCashControl(new CacheControl().setNoStore(true))
                .setContent(new Content("application/json", "{\"access_token\":\"" + response.getBearerToken() + "\"," +
                        "\"token_type\":\"Bearer\"" + ((response.getExpiresIn() > -1L) ? ",\"expires_in\":" + response.getExpiresIn() : "") +
                        (response.getRefreshToken() != null ? ",\"refresh_token\":\"" + response.getRefreshToken() + "\"" : "") +
                        (response.getScope() != null ? ",\"scope\":\"" + response.getScope() + "\"" : "") + "}"));
    }
}
