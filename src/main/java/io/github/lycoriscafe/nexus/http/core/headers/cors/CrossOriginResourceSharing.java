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
    private long accessControlMaxAge = -1L;
    private boolean accessControlAllowCredentials;
    private HashSet<HttpRequestMethod> accessControlAllowMethods;
    private HashSet<String> accessControlAllowHeaders;

    public CrossOriginResourceSharing setAccessControlAllowOrigin(final String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        return this;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public CrossOriginResourceSharing addAccessControlExposeHeader(final String accessControlExposeHeader) {
        if (this.accessControlExposeHeaders == null) accessControlExposeHeaders = new HashSet<>();
        accessControlExposeHeaders.add(accessControlExposeHeader);
        return this;
    }

    public List<String> getAccessControlExposeHeaders() {
        return accessControlAllowHeaders == null ? null : accessControlExposeHeaders.stream().toList();
    }

    public CrossOriginResourceSharing setAccessControlMaxAge(final long accessControlMaxAge) {
        this.accessControlMaxAge = accessControlMaxAge;
        return this;
    }

    public long getAccessControlMaxAge() {
        return accessControlMaxAge;
    }

    public CrossOriginResourceSharing setAccessControlAllowCredentials(final boolean accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
        return this;
    }

    public boolean isAccessControlAllowCredentials() {
        return accessControlAllowCredentials;
    }

    public CrossOriginResourceSharing addAccessControlAllowMethod(final HttpRequestMethod accessControlAllowMethod) {
        if (this.accessControlAllowMethods == null) accessControlAllowMethods = new HashSet<>();
        accessControlAllowMethods.add(accessControlAllowMethod);
        return this;
    }

    public List<HttpRequestMethod> getAccessControlAllowMethods() {
        return accessControlAllowMethods == null ? null : accessControlAllowMethods.stream().toList();
    }

    public CrossOriginResourceSharing addAccessControlAllowHeader(final String accessControlAllowHeader) {
        if (accessControlAllowHeaders == null) accessControlAllowHeaders = new HashSet<>();
        accessControlAllowHeaders.add(accessControlAllowHeader);
        return this;
    }

    public List<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders == null ? null : accessControlAllowHeaders.stream().toList();
    }

    public static String processOutgoingCORS(final CrossOriginResourceSharing crossOriginResourceSharing) {
        if (crossOriginResourceSharing == null) return "";

        StringBuilder output = new StringBuilder();
        if (crossOriginResourceSharing.getAccessControlAllowOrigin() != null) {
            output.append("Access-Control-Allow-Origin:").append(" ")
                    .append(crossOriginResourceSharing.getAccessControlAllowOrigin()).append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlExposeHeaders() != null) {
            output.append("Access-Control-Expose-Headers:");
            for (int i = 0; i < crossOriginResourceSharing.getAccessControlExposeHeaders().size(); i++) {
                output.append(" ").append(crossOriginResourceSharing.getAccessControlExposeHeaders().get(i));
                if (i != crossOriginResourceSharing.getAccessControlExposeHeaders().size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlMaxAge() > -1L) {
            output.append("Access-Control-Max-Age:").append(" ")
                    .append(crossOriginResourceSharing.getAccessControlMaxAge()).append("\r\n");
        }

        if (crossOriginResourceSharing.isAccessControlAllowCredentials()) {
            output.append("Access-Control-Allow-Credentials:").append(" ").append("true").append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlAllowMethods() != null) {
            output.append("Access-Control-Allow-Methods:");
            for (int i = 0; i < crossOriginResourceSharing.getAccessControlAllowMethods().size(); i++) {
                output.append(" ").append(crossOriginResourceSharing.getAccessControlAllowMethods().get(i).toString());
                if (i != crossOriginResourceSharing.getAccessControlAllowMethods().size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }

        if (crossOriginResourceSharing.getAccessControlAllowHeaders() != null) {
            output.append("Access-Control-Allow-Headers:");
            for (int i = 0; i < crossOriginResourceSharing.getAccessControlAllowHeaders().size(); i++) {
                output.append(" ").append(crossOriginResourceSharing.getAccessControlAllowHeaders().get(i));
                if (i != crossOriginResourceSharing.getAccessControlAllowHeaders().size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }
        return output.toString();
    }
}
