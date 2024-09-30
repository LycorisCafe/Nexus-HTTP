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
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HTTPResponse {
    private final long RESPONSE_ID;
    private final HTTPStatusCode status;
    private final Map<String, List<String>> headers;
    private final List<Cookie> cookies;
    private final Object content;

    private HTTPResponse(HTTPResponseBuilder builder) {
        this.RESPONSE_ID = builder.RESPONSE_ID;
        this.status = builder.status;
        this.headers = builder.headers;
        this.cookies = builder.cookies;
        this.content = builder.content;
    }

    public long getResponseId() {
        return RESPONSE_ID;
    }

    public HTTPStatusCode getStatus() {
        return status;
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

    public static HTTPResponseBuilder builder(long RESPONSE_ID) {
        return new HTTPResponseBuilder(RESPONSE_ID);
    }

    public static class HTTPResponseBuilder {
        private final long RESPONSE_ID;
        private HTTPStatusCode status;
        private final Map<String, List<String>> headers;
        private final List<Cookie> cookies;
        private Object content;

        public HTTPResponseBuilder(long RESPONSE_ID) {
            this.RESPONSE_ID = RESPONSE_ID;
            headers = new HashMap<>();
            cookies = new ArrayList<>();
        }

        public HTTPResponseBuilder status(HTTPStatusCode status) {
            this.status = status;
            return this;
        }

        public HTTPResponseBuilder header(String name, List<String> values) {
            headers.put(name, values);
            return this;
        }

        public HTTPResponseBuilder cookie(Cookie cookie) {
            cookies.add(cookie);
            return this;
        }

        public HTTPResponseBuilder content(Object content) throws IllegalArgumentException {
            if (!(content instanceof byte[] || content instanceof File)) {
                throw new IllegalArgumentException("Content must be a byte array or a file. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.content = content;
            return this;
        }

        public HTTPResponse build() {
            return new HTTPResponse(this);
        }
    }
}