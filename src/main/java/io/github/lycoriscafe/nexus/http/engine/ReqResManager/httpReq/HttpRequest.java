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

package io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq;

import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed class HttpRequest
        permits HttpGetRequest, HttpPostRequest, HttpHeadRequest {
    private final long requestId;
    private final String endpoint;
    private Map<String, String> parameters;
    private List<Header> headers;
    private List<Cookie> cookies;

    public HttpRequest(final long requestId,
                       final String endpoint) {
        this.requestId = requestId;
        this.endpoint = endpoint;
    }

    public void setParameter(final String key,
                             final String value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
    }

    public void setHeader(final Header header) {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        headers.add(header);
    }

    public void setCookie(final Cookie cookie) {
        if (cookie == null) {
            cookies = new ArrayList<>();
        }
        cookies.add(cookie);
    }

    public long getRequestId() {
        return requestId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
}
