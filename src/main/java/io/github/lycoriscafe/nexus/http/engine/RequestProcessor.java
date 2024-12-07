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
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            requestConsumer.dropConnection(requestId, HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED);
            return;
        }

        HttpRequest httpRequest = switch (HttpRequestMethod.validate(request[0].trim())) {
            case CONNECT, TRACE -> {
                requestConsumer.dropConnection(requestId, HttpStatusCode.NOT_IMPLEMENTED);
                yield null;
            }
            case DELETE -> new HttpDeleteRequest(requestConsumer, requestId, HttpRequestMethod.DELETE);
            case GET -> new HttpGetRequest(requestConsumer, requestId, HttpRequestMethod.GET);
            case HEAD -> new HttpHeadRequest(requestConsumer, requestId, HttpRequestMethod.HEAD);
            case OPTIONS -> new HttpOptionsRequest(requestConsumer, requestId, HttpRequestMethod.OPTIONS);
            case PATCH -> new HttpPatchRequest(requestConsumer, requestId, HttpRequestMethod.PATCH);
            case POST -> new HttpPostRequest(requestConsumer, requestId, HttpRequestMethod.POST);
            case PUT -> new HttpPutRequest(requestConsumer, requestId, HttpRequestMethod.PUT);
            case null -> {
                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST);
                yield null;
            }
        };

        if (httpRequest == null) {
            return;
        }

        String[] uriParts = request[1].split("\\?", 0);
        switch (uriParts.length) {
            case 1 -> httpRequest.setEndpoint(decodeUri(uriParts[0]));
            case 2 -> {
                httpRequest.setEndpoint(decodeUri(uriParts[0]));
                httpRequest.setParameters(decodeParams(uriParts[1]));
            }
            default -> {
                requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST);
                return;
            }
        }

        for (String header : headers) {
            String[] parts = header.split(":", 2);
            String headerName = parts[0].toLowerCase(Locale.US).trim();
            if (headerName.equals("cookie")) {
                httpRequest.setCookies(Cookie.parseIncomingCookies(parts[1]));
            } else if (headerName.equals("authorization")) {
                httpRequest.setAuthorization(Authorization.processIncomingAuth(parts[1]));
            } else {
                httpRequest.setHeader(Header.parseIncomingHeader(parts));
            }
        }

        httpRequest.finalizeRequest();
    }

    private static Map<String, String> decodeParams(final String params) {
        Map<String, String> decodedParams = new HashMap<>();
        String[] parts = params.split("&", 0);
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            decodedParams.put(decodeUri(keyValue[0]), decodeUri(keyValue[1]));
        }
        return decodedParams;
    }

    private static String decodeUri(final String uri) {
        return URLDecoder.decode(uri.trim(), StandardCharsets.UTF_8);
    }
}
