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
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * HTTP GET request method.
 *
 * @apiNote Since {@code GET} request method don't accept content, all request headers beginning with {@code Content-} must be avoided.
 * @see GET
 * @see #finalizeRequest()
 * @see HttpRequest
 * @since v1.0.0
 */
public sealed class HttpGetRequest extends HttpRequest permits HttpDeleteRequest, HttpHeadRequest, HttpOptionsRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpGetRequest.class);

    /**
     * @param requestConsumer {@code RequestConsumer} bound to the HTTP request
     * @param requestId       Request id bound to the HTTP request
     * @param requestMethod   HTTP request method of the request
     * @see HttpGetRequest
     * @since v1.0.0
     */
    public HttpGetRequest(final RequestConsumer requestConsumer,
                          final long requestId,
                          final HttpRequestMethod requestMethod) {
        super(requestConsumer, requestId, requestMethod);
    }

    /**
     * This method will process the content related operations. Since {@code GET}, {@code DELETE}, {@code HEAD} and {@code OPTIONS} request methods
     * are not supporting the content related operations, it will cause a connection drop.
     *
     * @since v1.0.0
     */
    @Override
    public void finalizeRequest() {
        if (getHeaders() != null) {
            for (Header header : getHeaders()) {
                if (header.getName().toLowerCase(Locale.US).startsWith("content-")) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST, "content cannot be processed with provided request method", logger);
                    return;
                }
            }
        }
        super.finalizeRequest();
    }
}
