/*
 * Copyright 2025 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.DELETE;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

/**
 * HTTP DELETE request method.
 *
 * @apiNote Since {@code DELETE} request method doesn't accept content, all request headers beginning with {@code Content-} must be avoided.
 * @see DELETE
 * @see #finalizeRequest()
 * @see HttpRequest
 * @since v1.0.0
 */
public final class HttpDeleteRequest extends HttpGetRequest {
    /**
     * @param requestConsumer {@code RequestConsumer} bound to the HTTP request
     * @param requestId       Request id bound to the HTTP request
     * @param requestMethod   HTTP request method of the request
     * @see HttpDeleteRequest
     * @since v1.0.0
     */
    public HttpDeleteRequest(final RequestConsumer requestConsumer,
                             final long requestId,
                             final HttpRequestMethod requestMethod) {
        super(requestConsumer, requestId, requestMethod);
    }
}
