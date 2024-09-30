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

public final class HttpGetRequest extends HttpRequest {
    private final Object content;

    public HttpGetRequest(HttpGetRequestBuilder builder) {
        super(builder);
        content = builder.content;
    }

    public Object getContent() {
        return content;
    }

    public static HttpGetRequestBuilder builder(long RESPONSE_ID) {
        return new HttpGetRequestBuilder(RESPONSE_ID);
    }

    public static class HttpGetRequestBuilder extends HttpRequestBuilder {
        private Object content;

        public HttpGetRequestBuilder(final long RESPONSE_ID) {
            super(RESPONSE_ID);
        }

        public HttpRequestBuilder content(Object content) throws IllegalArgumentException {
            if (!(content instanceof byte[] || content instanceof File)) {
                throw new IllegalArgumentException("Content must be a byte array or a file. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.content = content;
            return this;
        }

        public HttpGetRequest build() {
            return new HttpGetRequest(this);
        }
    }
}
