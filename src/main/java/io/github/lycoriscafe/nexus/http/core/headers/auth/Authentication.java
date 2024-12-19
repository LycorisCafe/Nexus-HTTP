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

import java.util.List;

/**
 * Parent authentication class
 *
 * @see io.github.lycoriscafe.nexus.http.core.headers.auth
 * @see <a href="https://datatracker.ietf.org/doc/rfc7235">Hypertext Transfer Protocol (HTTP/1.1): Authentication (rfc 7235)</a>
 * @since v1.0.0
 */
public abstract class Authentication {
    /**
     * Process <code>WWW-Authenticate</code> HTTP headers for provided list of <code>Authentication</code> types.
     *
     * @param authentications List of <code>Authentication</code>
     * @return <code>WWW-Authenticate</code> header(s) string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Authentication
     * @since v1.0.0
     */
    public static String processOutgoingAuthentications(final List<Authentication> authentications) {
        if (authentications == null || authentications.isEmpty()) return "";

        StringBuilder output = new StringBuilder();
        for (Authentication authentication : authentications) {
            output.append("WWW-Authenticate:").append(" ").append(authentication.processOutgoingAuth()).append("\r\n");
        }
        return output.toString();
    }

    /**
     * Process instance-wise <code>WWW-Authenticate</code> HTTP header values.
     *
     * @return Header value string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Authentication
     * @since v1.0.0
     */
    public abstract String processOutgoingAuth();
}
