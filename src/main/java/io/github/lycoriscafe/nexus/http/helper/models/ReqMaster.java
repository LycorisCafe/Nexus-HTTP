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

import java.util.Locale;

public sealed class ReqMaster permits ReqEndpoint, ReqFile {
    private final String httpEndpoint;
    private final String reqEndpoint;
    private final HttpRequestMethod reqMethod;

    public ReqMaster(final String httpEndpoint,
                     final String reqEndpoint,
                     final HttpRequestMethod reqMethod) {
        this.httpEndpoint = httpEndpoint;
        this.reqEndpoint = reqEndpoint;
        this.reqMethod = reqMethod;
    }

    public String getEndpoint() {
        return httpEndpoint.equals("/") ? "" : httpEndpoint.toLowerCase(Locale.ROOT)
                + reqEndpoint.toLowerCase(Locale.ROOT);
    }

    public HttpRequestMethod getReqMethod() {
        return reqMethod;
    }
}
