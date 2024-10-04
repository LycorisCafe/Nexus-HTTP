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

import io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class HttpPostRequest extends HttpRequest {
    private final Authorization authorization;
    private final List<HttpPostContent> httpPostContents;

    public HttpPostRequest(HttpPostRequestBuilder builder) {
        super(builder);
        this.authorization = builder.authorization;
        this.httpPostContents = builder.httpPostContents;
    }

    public List<HttpPostContent> getHttpPostContents() {
        return httpPostContents;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public static HttpPostRequestBuilder builder(long RESPONSE_ID) {
        return new HttpPostRequestBuilder(RESPONSE_ID);
    }

    public static final class HttpPostRequestBuilder extends HttpRequestBuilder {
        private Authorization authorization;
        private final List<HttpPostContent> httpPostContents;

        public HttpPostRequestBuilder(final long RESPONSE_ID) {
            super(RESPONSE_ID);
            httpPostContents = new ArrayList<>();
        }

        public HttpPostRequestBuilder authorization(Authorization authorization) {
            if (authorization == null) {
                throw new IllegalArgumentException("Authorization cannot be null");
            }
            this.authorization = authorization;
            return this;
        }

        public HttpPostRequestBuilder content(String name, String fileName, Object content)
                throws IllegalArgumentException {
            if (!(content instanceof byte[] || content instanceof File)) {
                throw new IllegalArgumentException("Content must be a byte array or a file. " +
                        "If you need this to be null, just ignore this method.");
            }
            httpPostContents.add(new HttpPostContent(name, fileName, content));
            return this;
        }

        @Override
        public HttpPostRequest build() {
            return new HttpPostRequest(this);
        }
    }

    public record HttpPostContent(String name, String fileName, Object content) {
    }
}
