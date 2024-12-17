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

import io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication;

public final class BearerAuthentication extends Authentication {
    private BearerError error;
    private String realm;
    private String scope;
    private String errorDescription;
    private String errorURI;

    public BearerAuthentication(final BearerError error) {
        this.error = error;
    }

    public BearerAuthentication(final String realm) {
        this.realm = realm;
    }

    public BearerError getError() {
        return error;
    }

    public BearerAuthentication setError(final BearerError error) {
        this.error = error;
        return this;
    }

    public String getRealm() {
        return realm;
    }

    public BearerAuthentication setRealm(final String realm) {
        this.realm = realm;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public BearerAuthentication setScope(final String scope) {
        this.scope = scope;
        return this;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public BearerAuthentication setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }

    public String getErrorURI() {
        return errorURI;
    }

    public BearerAuthentication setErrorURI(final String errorURI) {
        this.errorURI = errorURI;
        return this;
    }

    @Override
    public String processOutgoingAuth() {
        StringBuilder output = new StringBuilder().append("Bearer ");
        if (error != null) output.append("error=\"").append(error.getValue()).append("\"").append(", realm=\"").append(realm).append("\"");
        else output.append("realm=\"").append(realm).append("\"");

        if (scope != null) output.append(", scope=\"").append(scope).append("\"");
        if (errorDescription != null) output.append(", error-description=\"").append(errorDescription).append("\"");
        if (errorURI != null) output.append(", error-uri=\"").append(errorURI).append("\"");
        return output.toString();
    }
}
