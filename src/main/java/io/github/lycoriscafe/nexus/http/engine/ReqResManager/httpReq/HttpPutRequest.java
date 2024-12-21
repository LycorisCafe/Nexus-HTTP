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

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.PUT;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

/**
 * HTTP PUT request method.
 *
 * @see PUT
 * @see HttpRequest
 * @since v1.0.0
 */
public final class HttpPutRequest extends HttpPostRequest {
    public HttpPutRequest(final RequestConsumer requestConsumer,
                          final long requestId,
                          final HttpRequestMethod requestMethod) {
        super(requestConsumer, requestId, requestMethod);
    }
}
