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

public final class BearerAuthentication {
    private final BearerError error;
    private String realm;
    private String scope;
    private String errorDescription;
    private String errorURI;

    public BearerAuthentication(final BearerError error) {
        this.error = error;
    }

    public BearerError getError() {
        return error;
    }

    public String getRealm() {
        return realm;
    }

    public BearerAuthentication setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public BearerAuthentication setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public BearerAuthentication setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }

    public String getErrorURI() {
        return errorURI;
    }

    public BearerAuthentication setErrorURI(String errorURI) {
        this.errorURI = errorURI;
        return this;
    }
}
