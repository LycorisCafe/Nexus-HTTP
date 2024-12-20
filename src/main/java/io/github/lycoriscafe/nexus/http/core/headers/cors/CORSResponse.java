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
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.util.List;

/**
 * Cross Origin Resource Sharing (CORS) for HTTP response.
 * <pre>
 *     {@code
 *     // Example code
 *     var cors = new CORSResponse()
 *          .setAccessControlAllowOrigin("someOrigin");
 *     }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">Cross-Origin Resource Sharing (MDN Docs)</a>
 * @since v1.0.0
 */
public final class CORSResponse {
    private String accessControlAllowOrigin;
    private List<String> accessControlExposeHeaders;
    private Long accessControlMaxAge;
    private boolean accessControlAllowCredentials;
    private List<HttpRequestMethod> accessControlAllowMethods;
    private List<String> accessControlAllowHeaders;

    /**
     * Set <code>Access-Control-Allow-Origin</code> header value.
     *
     * @param accessControlAllowOrigin Allow origin
     * @return Same <code>CORSResponse</code> instance
     * @see CORSResponse
     * @since v1.0.0
     */
    public CORSResponse setAccessControlAllowOrigin(final String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        return this;
    }

    /**
     * Get <code>Access-Control-Allow-Origin</code> header value.
     *
     * @return Allowed origin
     * @see #setAccessControlAllowOrigin(String)
     * @see CORSResponse
     * @since v1.0.0
     */
    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    /**
     * Add a value to <code>Access-Control-Expose-Headers</code> header.
     *
     * @param accessControlExposeHeader Expose header
     * @return Same <code>CORSResponse</code> instance
     * @see CORSResponse
     * @since v1.0.0
     */
    public CORSResponse addAccessControlExposeHeader(final String accessControlExposeHeader) {
        if (this.accessControlExposeHeaders == null) accessControlExposeHeaders = new NonDuplicateList<>();
        accessControlExposeHeaders.add(accessControlExposeHeader);
        return this;
    }

    /**
     * Get <code>Access-Control-Expose-Headers</code> header values.
     *
     * @return Exposed headers
     * @see #addAccessControlExposeHeader(String)
     * @see CORSResponse
     * @since v1.0.0
     */
    public List<String> getAccessControlExposeHeaders() {
        return accessControlAllowHeaders == null ? null : accessControlExposeHeaders.stream().toList();
    }

    /**
     * Set <code>Access-Control-Max-Age</code> header value in seconds.
     *
     * @param accessControlMaxAge Max age in seconds
     * @return Same <code>CORSResponse</code> instance
     * @see CORSResponse
     * @since v1.0.0
     */
    public CORSResponse setAccessControlMaxAge(final long accessControlMaxAge) {
        this.accessControlMaxAge = accessControlMaxAge;
        return this;
    }

    /**
     * Get <code>Access-Control-Max-Age</code> header value.
     *
     * @return Max age
     * @see #setAccessControlMaxAge(long)
     * @see CORSResponse
     * @since v1.0.0
     */
    public Long getAccessControlMaxAge() {
        return accessControlMaxAge;
    }

    /**
     * Set <code>Access-Control-Allow-Credentials</code> header.
     *
     * @param accessControlAllowCredentials Allow credentials header status
     * @return Same <code>CORSResponse</code> instance
     * @see CORSResponse
     * @since v1.0.0
     */
    public CORSResponse setAccessControlAllowCredentials(final boolean accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
        return this;
    }

    /**
     * Get <code>Access-Control-Allow-Credentials</code> header status.
     *
     * @return Allow credentials status
     * @see #setAccessControlAllowCredentials(boolean)
     * @see CORSResponse
     * @since v1.0.0
     */
    public boolean isAccessControlAllowCredentials() {
        return accessControlAllowCredentials;
    }

    /**
     * Add a value to <code>Access-Control-Allow-Methods</code> header.
     *
     * @param accessControlAllowMethod Allow method
     * @return Same <code>CORSResponse</code> instance
     * @see CORSResponse
     * @since v1.0.0
     */
    public CORSResponse addAccessControlAllowMethod(final HttpRequestMethod accessControlAllowMethod) {
        if (this.accessControlAllowMethods == null) accessControlAllowMethods = new NonDuplicateList<>();
        accessControlAllowMethods.add(accessControlAllowMethod);
        return this;
    }

    /**
     * Get <code>Access-Control-Allow-Method</code> header values.
     *
     * @return Allowed methods
     * @see #addAccessControlAllowMethod(HttpRequestMethod)
     * @see CORSResponse
     * @since v1.0.0
     */
    public List<HttpRequestMethod> getAccessControlAllowMethods() {
        return accessControlAllowMethods == null ? null : accessControlAllowMethods.stream().toList();
    }

    /**
     * Add a value to <code>Access-Control-Allow-Headers</code> header.
     *
     * @param accessControlAllowHeader Allow header
     * @return Same <code>CORSResponse</code> instance
     * @see CORSResponse
     * @since v1.0.0
     */
    public CORSResponse addAccessControlAllowHeader(final String accessControlAllowHeader) {
        if (accessControlAllowHeaders == null) accessControlAllowHeaders = new NonDuplicateList<>();
        accessControlAllowHeaders.add(accessControlAllowHeader);
        return this;
    }

    /**
     * Get <code>Access-Control-Allow-Headers</code> header values.
     *
     * @return Allowed headers
     * @see #addAccessControlAllowHeader(String)
     * @see CORSResponse
     * @since v1.0.0
     */
    public List<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders == null ? null : accessControlAllowHeaders.stream().toList();
    }

    /**
     * Process CORS instance to include in HTTP response headers.
     *
     * @param CORSResponse Instance that need to be processed
     * @return HTTP headers string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see CORSResponse
     * @since v1.0.0
     */
    public static String processOutgoingCORS(final CORSResponse CORSResponse) {
        if (CORSResponse == null) return "";

        StringBuilder output = new StringBuilder();
        if (CORSResponse.getAccessControlAllowOrigin() != null) {
            output.append("Access-Control-Allow-Origin:").append(" ").append(CORSResponse.getAccessControlAllowOrigin()).append("\r\n");
        }

        if (CORSResponse.getAccessControlExposeHeaders() != null) {
            output.append("Access-Control-Expose-Headers:");
            for (int i = 0; i < CORSResponse.getAccessControlExposeHeaders().size(); i++) {
                output.append(" ").append(CORSResponse.getAccessControlExposeHeaders().get(i));
                if (i != CORSResponse.getAccessControlExposeHeaders().size() - 1) output.append(",");
            }
            output.append("\r\n");
        }

        if (CORSResponse.getAccessControlMaxAge() != null) {
            output.append("Access-Control-Max-Age:").append(" ").append(CORSResponse.getAccessControlMaxAge()).append("\r\n");
        }

        if (CORSResponse.isAccessControlAllowCredentials()) {
            output.append("Access-Control-Allow-Credentials:").append(" ").append("true").append("\r\n");
        }

        if (CORSResponse.getAccessControlAllowMethods() != null) {
            output.append("Access-Control-Allow-Methods:");
            for (int i = 0; i < CORSResponse.getAccessControlAllowMethods().size(); i++) {
                output.append(" ").append(CORSResponse.getAccessControlAllowMethods().get(i).toString());
                if (i != CORSResponse.getAccessControlAllowMethods().size() - 1) output.append(",");
            }
            output.append("\r\n");
        }

        if (CORSResponse.getAccessControlAllowHeaders() != null) {
            output.append("Access-Control-Allow-Headers:");
            for (int i = 0; i < CORSResponse.getAccessControlAllowHeaders().size(); i++) {
                output.append(" ").append(CORSResponse.getAccessControlAllowHeaders().get(i));
                if (i != CORSResponse.getAccessControlAllowHeaders().size() - 1) output.append(",");
            }
            output.append("\r\n");
        }
        return output.toString();
    }
}
