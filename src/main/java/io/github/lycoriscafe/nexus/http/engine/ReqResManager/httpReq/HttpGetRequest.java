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

import java.io.File;

public sealed class HttpGetRequest
        extends HttpRequest
        permits HttpDeleteRequest, HttpOptionsRequest, HttpPatchRequest, HttpPutRequest {
    private final Object content;

    public HttpGetRequest(HttpGetRequestBuilder builder) {
        super(builder);
        content = builder.content;
    }

    public Object getContent() {
        return content;
    }

    public static HttpGetRequestBuilder builder(long REQUEST_ID) {
        return new HttpGetRequestBuilder(REQUEST_ID);
    }

    public static sealed class HttpGetRequestBuilder
            extends HttpRequestBuilder
            permits HttpDeleteRequest.HttpDeleteRequestBuilder, HttpOptionsRequest.HttpOptionsRequestBuilder, HttpPatchRequest.HttpPatchRequestBuilder, HttpPutRequest.HttpPutRequestBuilder {
        private Object content;

        public HttpGetRequestBuilder(final long REQUEST_ID) {
            super(REQUEST_ID);
        }

        public HttpRequestBuilder content(Object content) throws IllegalArgumentException {
            if (!(content instanceof byte[] || content instanceof File)) {
                throw new IllegalArgumentException("Content must be a byte array or a file. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.content = content;
            return this;
        }

        @Override
        public HttpGetRequest build() {
            return new HttpGetRequest(this);
        }
    }
}
