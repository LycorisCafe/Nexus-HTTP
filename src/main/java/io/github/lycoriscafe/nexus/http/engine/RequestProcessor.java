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

import io.github.lycoriscafe.nexus.http.core.HTTPVersion;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.methodProcessor.*;
import io.github.lycoriscafe.nexus.http.helper.HTTPServerConfiguration;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public final class RequestProcessor {
    private ExecutorService executorService;
    private final Map<HttpRequestMethod, MethodProcessor> methodProcessors;
    private final RequestHandler REQ_HANDLER;

    RequestProcessor(final RequestHandler REQ_HANDLER,
                     final BufferedInputStream INPUT_STREAM,
                     final HTTPServerConfiguration CONFIGURATION,
                     final Connection DATABASE) {
        this.REQ_HANDLER = REQ_HANDLER;

        // TODO http pipelining
//        if (CONFIGURATION.getHttpPipelineParallelCount() > 0) {
//            executorService = Executors.newFixedThreadPool(CONFIGURATION.getHttpPipelineParallelCount(),
//                    (CONFIGURATION.getThreadType() == ThreadType.PLATFORM ?
//                            Thread.ofPlatform().factory() : Thread.ofVirtual().factory()));
//        }

        methodProcessors = new HashMap<>();
        methodProcessors.put(HttpRequestMethod.GET, new GETProcessor(REQ_HANDLER, INPUT_STREAM, DATABASE, CONFIGURATION));
        methodProcessors.put(HttpRequestMethod.POST, new POSTProcessor(REQ_HANDLER, INPUT_STREAM, DATABASE, CONFIGURATION));
        methodProcessors.put(HttpRequestMethod.PUT, new PUTProcessor(REQ_HANDLER, INPUT_STREAM, DATABASE, CONFIGURATION));
        methodProcessors.put(HttpRequestMethod.DELETE, new DELETEProcessor(REQ_HANDLER, DATABASE));
        methodProcessors.put(HttpRequestMethod.PATCH, new PATCHProcessor(REQ_HANDLER, INPUT_STREAM, DATABASE, CONFIGURATION));
    }

    void processRequest(final long REQUEST_ID,
                        final ArrayList<Object> REQUEST_LINE,
                        final Map<String, List<String>> HEADERS) {
        HttpRequest<?> httpRequest = new HttpRequest<>(REQUEST_ID);
        httpRequest.setRequestMethod((HttpRequestMethod) REQUEST_LINE.getFirst());
        httpRequest.setRequestURL(REQUEST_LINE.get(1).toString().contains("?") ?
                REQUEST_LINE.get(1).toString().split("\\?")[0] :
                REQUEST_LINE.get(1).toString());
        if (REQUEST_LINE.get(1).toString().contains("?")) {
            Map<String, String> params = new HashMap<>();
            for (String param : REQUEST_LINE.get(1).toString().split("\\?")[1].split("&")) {
                params.put(param.split("=")[0], param.split("=")[1]);
            }
            httpRequest.setParameters(params);
        }
        httpRequest.setVersion((HTTPVersion) REQUEST_LINE.getLast());
        httpRequest.setHeaders(HEADERS);

        HttpResponse<?> httpResponse = switch (httpRequest.getRequestMethod()) {
            case GET, HEAD -> methodProcessors.get(HttpRequestMethod.GET).process(httpRequest);
            case POST -> methodProcessors.get(HttpRequestMethod.POST).process(httpRequest);
            case PUT -> methodProcessors.get(HttpRequestMethod.PUT).process(httpRequest);
            case DELETE -> methodProcessors.get(HttpRequestMethod.DELETE).process(httpRequest);
            case PATCH -> methodProcessors.get(HttpRequestMethod.PATCH).process(httpRequest);
            case TRACE -> {
                REQ_HANDLER.processBadRequest(REQUEST_ID, HTTPStatusCode.METHOD_NOT_ALLOWED);
                yield null;
            }
            default -> {
                REQ_HANDLER.processBadRequest(REQUEST_ID, HTTPStatusCode.NOT_IMPLEMENTED);
                yield null;
            }
        };

        if (httpResponse != null) {
            httpResponse.setVersion(HTTPVersion.HTTP_1_1);
            httpResponse.formatProtocol();
            if (httpRequest.getRequestMethod() == HttpRequestMethod.HEAD) {
                httpResponse.emptyContent();
            }
            REQ_HANDLER.addToSendQue(httpResponse);
        }
    }
}
