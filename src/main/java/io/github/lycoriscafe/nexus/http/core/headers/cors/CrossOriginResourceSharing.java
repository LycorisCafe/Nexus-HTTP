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

package io.github.lycoriscafe.nexus.http.core.headers.cors;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;

import java.util.HashSet;

public final class CrossOriginResourceSharing {
    private String accessControlAllowOrigin;
    private HashSet<String> accessControlExposeHeaders;
    private long accessControlMaxAge;
    private boolean accessControlAllowCredentials;
    private HashSet<HttpRequestMethod> accessControlAllowMethods;
    private HashSet<String> accessControlAllowHeaders;

    private CrossOriginResourceSharing accessControlAllowOrigin(final String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        return this;
    }

    public CrossOriginResourceSharing accessControlExposeHeader(final String accessControlExposeHeader) {
        if (this.accessControlExposeHeaders == null) {
            this.accessControlExposeHeaders = new HashSet<>();
        }
        accessControlExposeHeaders.add(accessControlExposeHeader);
        return this;
    }

    public CrossOriginResourceSharing accessControlMaxAge(final long accessControlMaxAge) {
        this.accessControlMaxAge = accessControlMaxAge;
        return this;
    }

    public CrossOriginResourceSharing accessControlAllowCredentials(final boolean accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
        return this;
    }

    public CrossOriginResourceSharing accessControlAllowMethod(final HttpRequestMethod accessControlAllowMethod) {
        if (this.accessControlAllowMethods == null) {
            this.accessControlAllowMethods = new HashSet<>();
        }
        accessControlAllowMethods.add(accessControlAllowMethod);
        return this;
    }

    public CrossOriginResourceSharing accessControlAllowHeader(final String accessControlAllowHeader) {
        if (this.accessControlAllowHeaders == null) {
            this.accessControlAllowHeaders = new HashSet<>();
        }
        accessControlAllowHeaders.add(accessControlAllowHeader);
        return this;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public HashSet<String> getAccessControlExposeHeaders() {
        return accessControlExposeHeaders;
    }

    public long getAccessControlMaxAge() {
        return accessControlMaxAge;
    }

    public boolean isAccessControlAllowCredentials() {
        return accessControlAllowCredentials;
    }

    public HashSet<HttpRequestMethod> getAccessControlAllowMethods() {
        return accessControlAllowMethods;
    }

    public HashSet<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders;
    }
}
