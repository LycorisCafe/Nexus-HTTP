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

public final class BearerTokenRequest {
    private final BearerGrantType grantType;
    private String username;
    private String password;
    private String refreshToken;

    public BearerTokenRequest(final BearerGrantType grantType) {
        this.grantType = grantType;
    }

    public BearerGrantType getGrantType() {
        return grantType;
    }

    public String getUsername() {
        return username;
    }

    public BearerTokenRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public BearerTokenRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public BearerTokenRequest setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
