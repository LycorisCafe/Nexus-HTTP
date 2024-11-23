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

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.*;

import java.io.BufferedReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class RequestProcessor {
    private final RequestConsumer requestConsumer;

    public RequestProcessor(final RequestConsumer requestConsumer) {
        this.requestConsumer = requestConsumer;
    }

    public void process(final long requestId,
                        final List<String> headers) {
        String[] request = headers.getFirst().split(" ");
        HttpRequest httpRequest = switch (HttpRequestMethod.validate(request[0].trim())) {
            case null -> // TODO Handle [Not Implemented]
                    null;
            case CONNECT -> // TODO Implement CONNECT
                    null;
            case DELETE -> new HttpDeleteRequest(requestId, uriDecoder(request[1]));
            case GET -> new HttpGetRequest(requestId, uriDecoder(request[1]));
            case HEAD -> new HttpHeadRequest(requestId, uriDecoder(request[1]));
            case OPTIONS -> new HttpOptionsRequest(requestId, uriDecoder(request[1]));
            case PATCH -> new HttpPatchRequest(requestId, uriDecoder(request[1]));
            case POST -> new HttpPostRequest(requestId, uriDecoder(request[1]));
            case PUT -> new HttpPutRequest(requestId, uriDecoder(request[1]));
            case TRACE -> // TODO Implement TRACE
                    null;
        };
    }

    private String uriDecoder(final String uri) {
        return URLDecoder.decode(uri.trim(), StandardCharsets.UTF_8);
    }
}
