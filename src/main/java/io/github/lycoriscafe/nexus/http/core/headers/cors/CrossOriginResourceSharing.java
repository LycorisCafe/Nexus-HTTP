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

import java.util.ArrayList;
import java.util.List;

public final class CrossOriginResourceSharing {
    private final String accessControlAllowOrigin;
    private final List<String> accessControlExposeHeaders;
    private final long accessControlMaxAge;
    private final boolean accessControlAllowCredentials;
    private final List<HttpRequestMethod> accessControlAllowMethods;
    private final List<String> accessControlAllowHeaders;

    public CrossOriginResourceSharing(CrossOriginResourceSharingBuilder builder) {
        this.accessControlAllowOrigin = builder.accessControlAllowOrigin;
        this.accessControlExposeHeaders = builder.accessControlExposeHeaders;
        this.accessControlMaxAge = builder.accessControlMaxAge;
        this.accessControlAllowCredentials = builder.accessControlAllowCredentials;
        this.accessControlAllowMethods = builder.accessControlAllowMethods;
        this.accessControlAllowHeaders = builder.accessControlAllowHeaders;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public List<String> getAccessControlExposeHeaders() {
        return accessControlExposeHeaders;
    }

    public long getAccessControlMaxAge() {
        return accessControlMaxAge;
    }

    public boolean isAccessControlAllowCredentials() {
        return accessControlAllowCredentials;
    }

    public List<HttpRequestMethod> getAccessControlAllowMethods() {
        return accessControlAllowMethods;
    }

    public List<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders;
    }

    public static CrossOriginResourceSharingBuilder builder() {
        return new CrossOriginResourceSharingBuilder();
    }

    public static class CrossOriginResourceSharingBuilder {
        private String accessControlAllowOrigin;
        private List<String> accessControlExposeHeaders;
        private long accessControlMaxAge;
        private boolean accessControlAllowCredentials;
        private List<HttpRequestMethod> accessControlAllowMethods;
        private List<String> accessControlAllowHeaders;

        public CrossOriginResourceSharingBuilder accessControlAllowOrigin(String accessControlAllowOrigin) {
            this.accessControlAllowOrigin = accessControlAllowOrigin;
            return this;
        }

        public CrossOriginResourceSharingBuilder accessControlExposeHeader(String accessControlExposeHeader) {
            if (this.accessControlExposeHeaders == null) {
                this.accessControlExposeHeaders = new ArrayList<>();
            }
            accessControlExposeHeaders.add(accessControlExposeHeader);
            return this;
        }

        public CrossOriginResourceSharingBuilder accessControlMaxAge(long accessControlMaxAge) {
            this.accessControlMaxAge = accessControlMaxAge;
            return this;
        }

        public CrossOriginResourceSharingBuilder accessControlAllowCredentials(boolean accessControlAllowCredentials) {
            this.accessControlAllowCredentials = accessControlAllowCredentials;
            return this;
        }

        public CrossOriginResourceSharingBuilder accessControlAllowMethod(HttpRequestMethod accessControlAllowMethod) {
            if (this.accessControlAllowMethods == null) {
                this.accessControlAllowMethods = new ArrayList<>();
            }
            accessControlAllowMethods.add(accessControlAllowMethod);
            return this;
        }

        public CrossOriginResourceSharingBuilder accessControlAllowHeader(String accessControlAllowHeader) {
            if (this.accessControlAllowHeaders == null) {
                this.accessControlAllowHeaders = new ArrayList<>();
            }
            accessControlAllowHeaders.add(accessControlAllowHeader);
            return this;
        }

        public CrossOriginResourceSharing build() {
            return new CrossOriginResourceSharing(this);
        }
    }
}
