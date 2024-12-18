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

public sealed class ReqMaster permits ReqEndpoint, ReqFile {
    private final String requestEndpoint;
    private final HttpRequestMethod reqMethod;
    private final boolean authenticated;

    public ReqMaster(final String requestEndpoint,
                     final HttpRequestMethod reqMethod,
                     final boolean authenticated) {
        this.requestEndpoint = parseEndpoint(requestEndpoint);
        this.reqMethod = reqMethod;
        this.authenticated = authenticated;
    }

    public String getRequestEndpoint() {
        return requestEndpoint;
    }

    public HttpRequestMethod getReqMethod() {
        return reqMethod;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private static String parseEndpoint(final String requestEndpoint) {
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
