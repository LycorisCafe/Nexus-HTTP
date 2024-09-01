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

package io.github.lycoriscafe.nexus.http.connHelper;

import io.github.lycoriscafe.nexus.http.configuration.Database;
import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.connHelper.methodProcessors.GETProcessor;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.HTTPVersion;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.HTTPRequestMethod;

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
    private final ConnectionHandler CONN_HANDLER;
    private final Connection DATABASE;
    private final int MAX_CONTENT_LENGTH;
    private final File TEMP_DIR;

    RequestProcessor(final ConnectionHandler CONN_HANDLER,
                     final HTTPServerConfiguration CONFIGURATION,
                     final Connection DATABASE) {
        this.CONN_HANDLER = CONN_HANDLER;
        this.DATABASE = DATABASE;
        MAX_CONTENT_LENGTH = CONFIGURATION.getMaxContentLength();
        TEMP_DIR = CONFIGURATION.getTempDirectory();

        if (CONFIGURATION.getHttpPipelineParallelCount() > 0) {
            executorService = Executors.newFixedThreadPool(CONFIGURATION.getHttpPipelineParallelCount(),
                    (CONFIGURATION.getThreadType() == ThreadType.PLATFORM ?
                            Thread.ofPlatform().factory() : Thread.ofVirtual().factory()));
        }
    }

    void process(final int REQUEST_ID,
                 final String[] REQUEST_LINE,
                 final Map<String, List<String>> HEADERS) {
        HTTPVersion httpVersion = switch (REQUEST_LINE[2]) {
            case String version when version.toUpperCase(Locale.ROOT).equals("HTTP/1.1") -> HTTPVersion.HTTP_1_1;
            default -> null;
        };
        // TODO handle http version unsupported

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
        // TODO handle http request method unsupported

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

        String[] targets = null;
        try {
            targets = Database.findEndpointLocation(DATABASE, "Req" + httpRequestMethod, reqLocation);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // TODO handle 404

        final HTTPRequest REQUEST = new HTTPRequest(REQUEST_ID, httpRequestMethod, parameters, httpVersion, HEADERS);
        final HTTPResponse<?> RESPONSE = new HTTPResponse<>(REQUEST_ID);

        switch (httpRequestMethod) {
//            case CONNECT -> {}
//            case DELETE -> {}
            case PUT -> new GETProcessor(executorService, CONN_HANDLER, MAX_CONTENT_LENGTH, TEMP_DIR,
                    targets, REQUEST, RESPONSE).process();
            // TODO handle other request methods
        }
    }
}
