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

package io.github.lycoriscafe.nexus.http.helper.models;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.helper.Database;

/**
 * Parent model for communicate endpoint data to/from endpoint methods to/from the database.
 *
 * @see ReqEndpoint
 * @see ReqFile
 * @see Database
 * @since v1.0.0
 */
public sealed class ReqMaster permits ReqEndpoint, ReqFile {
    private final String requestEndpoint;
    private final HttpRequestMethod reqMethod;
    private final boolean authenticated;

    /**
     * Create instance of {@code ReqMaster}.
     *
     * @param requestEndpoint Endpoint URI
     * @param reqMethod       HTTP request method
     * @param authenticated   Is the endpoint authenticated?
     * @see HttpRequestMethod
     * @see ReqMaster
     * @since v1.0.0
     */
    public ReqMaster(final String requestEndpoint,
                     final HttpRequestMethod reqMethod,
                     final boolean authenticated) {
        this.requestEndpoint = parseEndpoint(requestEndpoint);
        this.reqMethod = reqMethod;
        this.authenticated = authenticated;
    }

    /**
     * Get provided endpoint URI.
     *
     * @return Endpoint URI
     * @see ReqMaster
     * @since v1.0.0
     */
    public String getRequestEndpoint() {
        return requestEndpoint;
    }

    /**
     * Get provided HTTP request method.
     *
     * @return HTTP request method
     * @see ReqMaster
     * @since v1.0.0
     */
    public HttpRequestMethod getReqMethod() {
        return reqMethod;
    }

    /**
     * Get endpoint-authenticated status.
     *
     * @return Endpoint authenticated status
     * @see ReqMaster
     * @since v1.0.0
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Process endpoint URI. Rebuild the URI by removing unnecessary {@code /}.
     *
     * @param requestEndpoint Endpoint URI
     * @return Reconstructed endpoint URI
     * @see ReqMaster
     * @since v1.0.0
     */
    public static String parseEndpoint(final String requestEndpoint) {
        String[] parts = requestEndpoint.split("/", 0);
        StringBuilder reconstructed = new StringBuilder("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            reconstructed.append(parts[i]);
            if (i < parts.length - 1) reconstructed.append("/");
        }
        return reconstructed.toString();
    }
}
