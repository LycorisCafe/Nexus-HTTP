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

import io.github.lycoriscafe.nexus.http.configuration.Database;
import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.core.HTTPVersion;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HTTPRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.engine.methodProcessor.CommonProcessor;
import io.github.lycoriscafe.nexus.http.engine.methodProcessor.GETProcessor;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RequestProcessor {
    private ExecutorService executorService;
    private final RequestHandler REQ_HANDLER;
    private final Connection DATABASE;
    private final int MAX_CONTENT_LENGTH;
    private final File TEMP_DIR;

    RequestProcessor(final RequestHandler REQ_HANDLER,
                     final HTTPServerConfiguration CONFIGURATION,
                     final Connection DATABASE) {
        this.REQ_HANDLER = REQ_HANDLER;
        this.DATABASE = DATABASE;
        MAX_CONTENT_LENGTH = CONFIGURATION.getMaxContentLength();
        TEMP_DIR = CONFIGURATION.getTempDirectory();

        if (CONFIGURATION.getHttpPipelineParallelCount() > 0) {
            executorService = Executors.newFixedThreadPool(CONFIGURATION.getHttpPipelineParallelCount(),
                    (CONFIGURATION.getThreadType() == ThreadType.PLATFORM ?
                            Thread.ofPlatform().factory() : Thread.ofVirtual().factory()));
        }
    }

    void process(final long REQUEST_ID,
                 final String[] REQUEST_LINE,
                 final Map<String, List<String>> HEADERS) {
        final HTTPResponse<?> RESPONSE = new HTTPResponse<>(REQUEST_ID);

        HTTPVersion httpVersion = switch (REQUEST_LINE[2]) {
            case String version when version.toUpperCase(Locale.ROOT).equals("HTTP/1.1") -> HTTPVersion.HTTP_1_1;
            default -> null;
        };
        if (httpVersion == null) {
            RESPONSE.setStatusCode(HTTPStatusCode.HTTP_VERSION_NOT_SUPPORTED);
            REQ_HANDLER.addToSendQue(CommonProcessor.processErrors(RESPONSE));
            return;
        }

        HTTPRequestMethod httpRequestMethod = switch (REQUEST_LINE[0]) {
//            case String method when method.toUpperCase(Locale.ROOT).equals("CONNECT") -> HTTPRequestMethod.CONNECT;
//            case String method when method.toUpperCase(Locale.ROOT).equals("DELETE") -> HTTPRequestMethod.DELETE;
            case String method when method.toUpperCase(Locale.ROOT).equals("GET") -> HTTPRequestMethod.GET;
//            case String method when method.toUpperCase(Locale.ROOT).equals("HEAD") -> HTTPRequestMethod.HEAD;
//            case String method when method.toUpperCase(Locale.ROOT).equals("OPTIONS") -> HTTPRequestMethod.OPTIONS;
//            case String method when method.toUpperCase(Locale.ROOT).equals("PATCH") -> HTTPRequestMethod.PATCH;
//            case String method when method.toUpperCase(Locale.ROOT).equals("POST") -> HTTPRequestMethod.POST;
//            case String method when method.toUpperCase(Locale.ROOT).equals("PUT") -> HTTPRequestMethod.PUT;
//            case String method when method.toUpperCase(Locale.ROOT).equals("TRACE") -> HTTPRequestMethod.TRACE;
            default -> null;
        };
        if (httpRequestMethod == null) {
            RESPONSE.setStatusCode(HTTPStatusCode.NOT_EXTENDED);
            REQ_HANDLER.addToSendQue(CommonProcessor.processErrors(RESPONSE));
            return;
        }

        String reqLocation = REQUEST_LINE[1];
        Map<String, String> parameters = null;
        if (REQUEST_LINE[1].contains("?")) {
            String[] tempArray = REQUEST_LINE[1].splitWithDelimiters("\\?", 2);
            reqLocation = tempArray[0];
            tempArray = tempArray[2].split("&");

            parameters = new HashMap<>();
            for (String temp : tempArray) {
                String[] keyValue = temp.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }

        String[] targets;
        try {
            targets = Database.findEndpointLocation(DATABASE, "Req" + httpRequestMethod, reqLocation);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (targets == null) {
            RESPONSE.setStatusCode(HTTPStatusCode.NOT_FOUND);
            REQ_HANDLER.addToSendQue(CommonProcessor.processErrors(RESPONSE));
            return;
        }

        final HTTPRequest REQUEST = new HTTPRequest(REQUEST_ID, httpRequestMethod, parameters, httpVersion, HEADERS);

        switch (httpRequestMethod) {
//            case CONNECT -> {}
//            case DELETE -> {}
            case PUT -> new GETProcessor(executorService, REQ_HANDLER, MAX_CONTENT_LENGTH, TEMP_DIR,
                    targets, REQUEST, RESPONSE).process();
            // TODO handle other request methods
        }
    }
}
