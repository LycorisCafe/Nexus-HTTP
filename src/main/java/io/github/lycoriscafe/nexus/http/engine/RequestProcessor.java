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

import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.core.HTTPVersion;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HTTPRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.engine.methodProcessor.GETProcessor;
import io.github.lycoriscafe.nexus.http.engine.methodProcessor.MethodProcessor;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public final class RequestProcessor {
    private ExecutorService executorService;
    private final Map<HTTPRequestMethod, MethodProcessor> methodProcessors;
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
        methodProcessors.put(HTTPRequestMethod.GET, new GETProcessor(REQ_HANDLER, INPUT_STREAM, DATABASE));
    }

    void processRequest(final long REQUEST_ID,
                        final ArrayList<Object> REQUEST_LINE,
                        final Map<String, List<String>> HEADERS) {
        HTTPRequest<?> httpRequest = new HTTPRequest<>(REQUEST_ID);
        httpRequest.setRequestMethod((HTTPRequestMethod) REQUEST_LINE.getFirst());
        httpRequest.setRequestURL(REQUEST_LINE.get(2).toString().contains("?") ?
                REQUEST_LINE.get(2).toString().split("\\?")[0] :
                REQUEST_LINE.get(2).toString());
        if (REQUEST_LINE.get(2).toString().contains("?")) {
            Map<String, String> params = new HashMap<>();
            for (String param : REQUEST_LINE.get(2).toString().split("&")) {
                params.put(param.split("=")[0], param.split("=")[1]);
            }
            httpRequest.setParameters(params);
        }
        httpRequest.setVersion((HTTPVersion) REQUEST_LINE.getLast());
        httpRequest.setHeaders(HEADERS);

        HTTPResponse<?> httpResponse = switch (httpRequest.getRequestMethod()) {
            case HTTPRequestMethod.GET -> ((GETProcessor) methodProcessors.get(HTTPRequestMethod.GET))
                    .process(httpRequest);
            default -> {
                REQ_HANDLER.processBadRequest(REQUEST_ID, HTTPStatusCode.BAD_REQUEST);
                yield null;
            }
        };

        if (httpResponse != null) {
            RequestHandler.addDefaultHeaders(httpResponse);
            REQ_HANDLER.addToSendQue(httpResponse);
        }
    }
}
