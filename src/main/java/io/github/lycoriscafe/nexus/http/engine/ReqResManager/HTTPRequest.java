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

package io.github.lycoriscafe.nexus.http.engine.ReqResManager;

import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HTTPRequest {
    private final long REQUEST_ID;
    private final Map<String, String> parameters;
    private final Map<String, List<String>> headers;
    private final List<Cookie> cookies;
    private final Object content;

    private HTTPRequest(HTTPRequestBuilder builder) {
        this.REQUEST_ID = builder.REQUEST_ID;
        this.parameters = builder.parameters;
        this.headers = builder.headers;
        this.cookies = builder.cookies;
        this.content = builder.content;
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

    public Object getContent() {
        return content;
    }

    public static HTTPRequestBuilder builder(long RESPONSE_ID) {
        return new HTTPRequestBuilder(RESPONSE_ID);
    }

    public static class HTTPRequestBuilder {
        private final long REQUEST_ID;
        private Map<String, String> parameters;
        private final Map<String, List<String>> headers;
        private final List<Cookie> cookies;
        private Object content;

        public HTTPRequestBuilder(long REQUEST_ID) {
            this.REQUEST_ID = REQUEST_ID;
            headers = new HashMap<>();
            cookies = new ArrayList<>();
        }

        public HTTPRequestBuilder status(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public HTTPRequestBuilder header(String name, List<String> values) {
            headers.put(name, values);
            return this;
        }

        public HTTPRequestBuilder cookie(Cookie cookie) {
            cookies.add(cookie);
            return this;
        }

        public HTTPRequestBuilder content(Object content) throws IllegalArgumentException {
            if (!(content instanceof byte[] || content instanceof File)) {
                throw new IllegalArgumentException("Content must be a byte array or a file. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.content = content;
            return this;
        }

        public HTTPRequest build() {
            return new HTTPRequest(this);
        }
    }
}
