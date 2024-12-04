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

import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.basic.BasicAuthorization;

import java.util.Locale;

public class Authorization {
    private final AuthScheme authScheme;

    public Authorization(final AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

    public AuthScheme getAuthScheme() {
        return authScheme;
    }

    public static Authorization processIncomingAuth(final String auth) {
        String[] parts = auth.split(" ", 2);
        return switch (parts[0].trim().toLowerCase(Locale.US)) {
            case "basic" -> BasicAuthorization.processIncomingAuth(parts[1]);
            default -> null;
        };
    }
}
