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

import io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication;

/**
 * The 'Basic' authentication for HTTP. An instance of this class will send to the clients when they asked to access to a protected resource without
 * specifying 'Authorization' header. Default charset that send with this is 'UTF-8'.
 * <pre>
 *     {@code
 *      <!-- General header format -->
 *      HTTP/1.1 401 Unauthorized
 *      WWW-Authenticate: Basic realm="specifiedRealm", charset="UTF-8"
 *      }
 * </pre>
 *
 * @see BasicAuthorization
 * @see io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication Authentication
 * @see io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization Authorization
 * @see <a href="https://datatracker.ietf.org/doc/rfc7617">The 'Basic' HTTP Authentication Scheme (rfc7617)</a>
 * @since v1.0.0
 */
public final class BasicAuthentication extends Authentication {
    private final String realm;

    /**
     * Create an instance of 'Basic' authentication.
     *
     * @param realm Realm for the target resource
     * @see BasicAuthentication
     * @since v1.0.0
     */
    public BasicAuthentication(final String realm) {
        this.realm = realm;
    }

    /**
     * Get realm of the provided instance.
     *
     * @return Specified realm
     * @see BasicAuthentication
     * @since v1.0.0
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Process authentication instance as <code>WWW-Authenticate</code> HTTP header value.
     *
     * @return HTTP header value string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BasicAuthentication
     * @since v1.0.0
     */
    @Override
    public String processOutgoingAuth() {
        return "Basic realm=\"" + realm + "\", charset=\"UTF-8\"";
    }
}
