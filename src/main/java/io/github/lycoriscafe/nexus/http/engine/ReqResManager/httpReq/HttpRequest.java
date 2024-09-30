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

import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed class HttpRequest
        permits HttpGetRequest, HttpPostRequest, HttpHeadRequest {
    private final long REQUEST_ID;
    private final Map<String, String> parameters;
    private final Map<String, List<String>> headers;
    private final List<Cookie> cookies;

    HttpRequest(HttpRequestBuilder builder) {
        this.REQUEST_ID = builder.REQUEST_ID;
        this.parameters = builder.parameters;
        this.headers = builder.headers;
        this.cookies = builder.cookies;
    }

    public long getRequestId() {
        return REQUEST_ID;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public static HttpRequestBuilder builder(long REQUEST_ID) {
        return new HttpRequestBuilder(REQUEST_ID);
    }

    public static sealed class HttpRequestBuilder
            permits HttpGetRequest.HttpGetRequestBuilder,
            HttpPostRequest.HttpPostRequestBuilder,
            HttpHeadRequest.HttpHeadRequestBuilder {
        private final long REQUEST_ID;
        private Map<String, String> parameters;
        private final Map<String, List<String>> headers;
        private final List<Cookie> cookies;

        public HttpRequestBuilder(long REQUEST_ID) {
            this.REQUEST_ID = REQUEST_ID;
            headers = new HashMap<>();
            cookies = new ArrayList<>();
        }

        public HttpRequestBuilder status(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public HttpRequestBuilder header(String name, List<String> values) {
            headers.put(name, values);
            return this;
        }

        public HttpRequestBuilder cookie(Cookie cookie) {
            cookies.add(cookie);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
