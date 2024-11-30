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

package io.github.lycoriscafe.nexus.http.engine;

import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public final class RequestProcessor {
    private final RequestConsumer requestConsumer;

    public RequestProcessor(final RequestConsumer requestConsumer) {
        this.requestConsumer = requestConsumer;
    }

    public void process(final long requestId,
                        final String requestLine,
                        final List<String> headers) {
        String[] request = requestLine.split(" ");

        if (!request[2].trim().equals("HTTP/1.1")) {
            requestConsumer.dropConnection(HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED);
            return;
        }

        HttpRequest httpRequest = switch (HttpRequestMethod.validate(request[0].trim())) {
            case CONNECT, TRACE -> {
                requestConsumer.dropConnection(HttpStatusCode.NOT_IMPLEMENTED);
                yield null;
            }
            case DELETE -> new HttpDeleteRequest(requestConsumer,
                    requestId, HttpRequestMethod.DELETE, uriDecoder(request[1]));
            case GET -> new HttpGetRequest(requestConsumer,
                    requestId, HttpRequestMethod.GET, uriDecoder(request[1]));
            case HEAD -> new HttpHeadRequest(requestConsumer,
                    requestId, HttpRequestMethod.HEAD, uriDecoder(request[1]));
            case OPTIONS -> new HttpOptionsRequest(requestConsumer,
                    requestId, HttpRequestMethod.OPTIONS, uriDecoder(request[1]));
            case PATCH -> new HttpPatchRequest(requestConsumer,
                    requestId, HttpRequestMethod.PATCH, uriDecoder(request[1]));
            case POST -> new HttpPostRequest(requestConsumer,
                    requestId, HttpRequestMethod.POST, uriDecoder(request[1]));
            case PUT -> new HttpPutRequest(requestConsumer,
                    requestId, HttpRequestMethod.PUT, uriDecoder(request[1]));
            case null -> {
                requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                yield null;
            }
        };

        if (httpRequest == null) {
            return;
        }

        for (String header : headers) {
            String[] parts = header.split(":", 2);
            if (parts[0].toLowerCase(Locale.US).trim().equals("cookie")) {
                httpRequest.setCookies(Cookie.processIncomingCookies(parts[1]));
            } else {
                httpRequest.setHeaders(Header.processIncomingHeader(parts));
            }
        }

        httpRequest.finalizeRequest();
    }

    private String uriDecoder(final String uri) {
        return URLDecoder.decode(uri.trim(), StandardCharsets.UTF_8);
    }
}
