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
    private final String endpoint;
    private final HttpRequestMethod reqMethod;

    public ReqMaster(final String endpoint, final HttpRequestMethod reqMethod) {
        this.endpoint = endpoint;
        this.reqMethod = reqMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public HttpRequestMethod getReqMethod() {
        return reqMethod;
    }
}
