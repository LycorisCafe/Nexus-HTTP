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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme;

public class BasicAuthentication implements Authentication {
    private final String realm;
    private String charset = "UTF-8";

    public BasicAuthentication(String realm) {
        this.realm = realm;
    }

    public BasicAuthentication setCharset(final String charset) {
        this.charset = charset;
        return this;
    }

    public String getRealm() {
        return realm;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public String processOutgoingAuth() {
        StringBuilder output = new StringBuilder().append("Basic").append(" ")
                .append("realm=\"").append(realm).append("\"");
        if (charset != null) {
            output.append(" ").append("charset=\"").append(charset).append("\"");
        }
        return output.toString();
    }
}
