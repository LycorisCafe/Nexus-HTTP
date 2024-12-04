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
import java.util.List;

public final class CrossOriginResourceSharing {
    private String accessControlAllowOrigin;
    private HashSet<String> accessControlExposeHeaders;
    private long accessControlMaxAge;
    private boolean accessControlAllowCredentials;
    private HashSet<HttpRequestMethod> accessControlAllowMethods;
    private HashSet<String> accessControlAllowHeaders;

    public CrossOriginResourceSharing accessControlAllowOrigin(final String accessControlAllowOrigin) {
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

    public List<HttpRequestMethod> getAccessControlAllowMethods() {
        return accessControlAllowMethods.stream().toList();
    }

    public List<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders.stream().toList();
    }

    public static String processOutgoingCORS(final CrossOriginResourceSharing crossOriginResourceSharing) {
        if (crossOriginResourceSharing == null) return "";

        StringBuilder output = new StringBuilder().append("Access-Control-Allow-Origin: ");
        if (crossOriginResourceSharing.getAccessControlAllowOrigin() != null) {
            output.append(crossOriginResourceSharing.getAccessControlAllowOrigin());
        } else {
            output.append("*");
        }
        output.append("\r\n");

        if (crossOriginResourceSharing.getAccessControlExposeHeaders() != null) {
            output.append("Access-Control-Expose-Headers:");
            List<String> exposeHeaders = crossOriginResourceSharing.getAccessControlExposeHeaders().stream().toList();
            for (int i = 0; i < exposeHeaders.size(); i++) {
                output.append(" ").append(exposeHeaders.get(i));
                if (i != exposeHeaders.size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlMaxAge() > 0) {
            output.append("Access-Control-Max-Age: ").append(crossOriginResourceSharing.getAccessControlMaxAge())
                    .append("\r\n");
        }

        if (crossOriginResourceSharing.isAccessControlAllowCredentials()) {
            output.append("Access-Control-Allow-Credentials: ").append("true").append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlAllowMethods() != null) {
            output.append("Access-Control-Allow-Methods:");
            List<HttpRequestMethod> allowMethods =
                    crossOriginResourceSharing.getAccessControlAllowMethods().stream().toList();
            for (int i = 0; i < allowMethods.size(); i++) {
                output.append(" ").append(allowMethods.get(i).toString());
                if (i != allowMethods.size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlAllowHeaders() != null) {
            output.append("Access-Control-Allow-Headers:");
            List<String> allowHeaders = crossOriginResourceSharing.getAccessControlExposeHeaders().stream().toList();
            for (int i = 0; i < allowHeaders.size(); i++) {
                output.append(" ").append(allowHeaders.get(i));
                if (i != allowHeaders.size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }

        return output.toString();
    }
}
