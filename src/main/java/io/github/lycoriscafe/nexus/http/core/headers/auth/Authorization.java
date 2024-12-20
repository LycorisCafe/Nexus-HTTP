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
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerAuthorization;

import java.util.Locale;

/**
 * Parent authorization class
 *
 * @see io.github.lycoriscafe.nexus.http.core.headers.auth
 * @since v1.0.0
 */
public class Authorization {
    private final AuthScheme authScheme;

    /**
     * Create an instance of <code>Authorization</code>.
     *
     * @param authScheme Authentication scheme
     * @see Authorization
     * @see <a href="https://datatracker.ietf.org/doc/rfc7235">Hypertext Transfer Protocol (HTTP/1.1): Authentication (rfc 7235)</a>
     * @since v1.0.0
     */
    public Authorization(final AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

    /**
     * Get provided authentication scheme
     *
     * @return Authentication scheme
     * @see AuthScheme
     * @see Authorization
     * @since v1.0.0
     */
    public AuthScheme getAuthScheme() {
        return authScheme;
    }

    /**
     * Process <code>Authorization</code> header values to <code>Authorization</code> types.
     *
     * @param auth <code>Authorization</code> header values
     * @return New instance of <code>Authorization</code> type
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Authorization
     * @since v1.0.0
     */
    public static Authorization processIncomingAuth(final String auth) {
        String[] parts = auth.trim().split(" ", 2);
        return switch (parts[0].trim().toLowerCase(Locale.US)) {
            case "basic" -> BasicAuthorization.processIncomingAuth(parts[1]);
            case "bearer" -> BearerAuthorization.processIncomingAuth(parts[1]);
            default -> null;
        };
    }
}
