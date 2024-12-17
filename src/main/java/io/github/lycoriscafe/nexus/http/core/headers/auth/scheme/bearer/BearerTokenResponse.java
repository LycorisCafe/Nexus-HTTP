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

public final class BearerTokenResponse {
    private final String bearerToken;
    private long expiresIn = -1L;
    private String refreshToken;
    private String scope;

    public BearerTokenResponse(final String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public BearerTokenResponse setExpiresIn(final long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public BearerTokenResponse setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getScope() {
        return scope;
    }

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
