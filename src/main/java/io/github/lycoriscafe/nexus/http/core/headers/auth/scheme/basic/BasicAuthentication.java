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

public final class BasicAuthentication extends Authentication {
    private final String realm;

    public BasicAuthentication(final String realm) {
        this.realm = realm;
    }

    public String getRealm() {
        return realm;
    }

    @Override
    public String processOutgoingAuth() {
        return "Basic realm=\"" + realm + "\", charset=\"UTF-8\"";
    }
}
