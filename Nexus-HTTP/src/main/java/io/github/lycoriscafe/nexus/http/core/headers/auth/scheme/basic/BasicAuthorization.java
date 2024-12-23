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

/**
 * The 'Basic' authorization for HTTP. An instance of this class will receive to the server endpoint when client provided the 'Authorization' header
 * with authentication scheme 'Basic'. The username and password will decode from Base64 to cleartext (charset UTF-8).
 * <pre>
 *     {@code
 *     <!-- General header format -->
 *     Authorization: Basic Base64EncodedUsernamePasswordPair
 *     }
 * </pre>
 *
 * @see Authorization
 * @see <a href="https://datatracker.ietf.org/doc/rfc7617">The 'Basic' HTTP Authentication Scheme (rfc7617)</a>
 * @since v1.0.0
 */
public final class BasicAuthorization extends Authorization {
    private final String username;
    private final String password;

    /**
     * Create an instance for 'Basic' authorization.
     *
     * @param username Cleartext username (charset UTF-8)
     * @param password Cleartext password (charset UTF-8)
     * @see BasicAuthorization
     * @since v1.0.0
     */
    public BasicAuthorization(final String username,
                              final String password) {
        super(AuthScheme.BASIC);
        this.username = username;
        this.password = password;
    }

    /**
     * Get cleartext username (charset UTF-8)
     *
     * @return Received username
     * @see BasicAuthorization
     * @since v1.0.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get cleartext password (charset UTF-8)
     *
     * @return Received password
     * @see BasicAuthorization
     * @since v1.0.0
     */
    public String getPassword() {
        return password;
    }

    /**
     * Process provided string to a new instance of {@code BasicAuthorization}
     *
     * @param params Basic authorization header value part (only data)
     * @return New instance of {@code BasicAuthorization}
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BasicAuthorization
     * @since v1.0.0
     */
    public static Authorization processIncomingAuth(final String params) {
        String[] auth = new String(Base64.getDecoder().decode(params), StandardCharsets.UTF_8).split(":", 2);
        return new BasicAuthorization(auth[0], auth[1]);
    }
}
