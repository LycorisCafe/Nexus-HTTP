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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.basic;

import io.github.lycoriscafe.nexus.http.core.headers.auth.AuthScheme;
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class BasicAuthorization extends Authorization {
    private final String username;
    private final String password;

    public BasicAuthorization(final String username,
                              final String password) {
        super(AuthScheme.Basic);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static Authorization processIncomingAuth(String params) {
        params = params.trim();
        String[] auth = new String(Base64.getDecoder().decode(params), StandardCharsets.UTF_8).split(":", 0);
        return new BasicAuthorization(auth[0], auth[1]);
    }
}
