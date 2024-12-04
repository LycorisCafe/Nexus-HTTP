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

import java.util.HashSet;
import java.util.List;

public class Authentication {
    private final HashSet<Authentication> authentications;

    public Authentication() {
        authentications = new HashSet<>();
    }

    public Authentication addAuthentication(final Authentication authentication) {
        if (authentication == null) throw new NullPointerException("authentication cannot be null");
        authentications.add(authentication);
        return this;
    }

    public List<Authentication> getAuthentications() {
        return authentications.stream().toList();
    }

    public static String processOutgoingAuthentication(final Authentication authentications) {
        if (authentications == null) return "";

        StringBuilder output = new StringBuilder();
        for (Authentication authentication : authentications.getAuthentications()) {
            output.append("WWW-Authenticate:").append(" ").append(authentication.processOutgoingAuth()).append("\r\n");
        }
        return output.toString();
    }

    /**
     * Reserved
     *
     * @return
     */
    public String processOutgoingAuth() {
        return null;
    }
}
