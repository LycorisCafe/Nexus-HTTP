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

public final class HttpPatchRequest extends HttpGetRequest {
    public HttpPatchRequest(HttpPatchRequestBuilder builder) {
        super(builder);
    }

    public static HttpPatchRequestBuilder builder(long REQUEST_ID) {
        return new HttpPatchRequestBuilder(REQUEST_ID);
    }

    public static final class HttpPatchRequestBuilder extends HttpGetRequestBuilder {
        public HttpPatchRequestBuilder(long REQUEST_ID) {
            super(REQUEST_ID);
        }

        @Override
        public HttpPatchRequest build() {
            return new HttpPatchRequest(this);
        }
    }
}
