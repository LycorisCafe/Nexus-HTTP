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

import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.HTTPVersion;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.HTTPRequestMethod;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RequestProcessor {
    private ExecutorService executorService;
    private final ConnectionHandler CONN_HANDLER;
    private final Connection DATABASE;

    RequestProcessor(final ConnectionHandler CONN_HANDLER,
                     final HTTPServerConfiguration CONFIGURATION,
                     final Connection DATABASE) {
        this.CONN_HANDLER = CONN_HANDLER;
        this.DATABASE = DATABASE;

        if (CONFIGURATION.isHttpPipelined()) {
            executorService = Executors.newFixedThreadPool(CONFIGURATION.getHttpPipelineParallelCount(),
                    (CONFIGURATION.getThreadType() == ThreadType.PLATFORM ?
                            Thread.ofPlatform().factory() : Thread.ofVirtual().factory()));
        }
    }

    void process(final int REQUEST_ID,
                 final String[] REQUEST_LINE,
                 final Map<String, List<String>> HEADERS) {
        HTTPRequest httpRequest;
        HTTPRequestMethod httpRequestMethod = switch (REQUEST_LINE[0]) {
            case String method when method.toUpperCase(Locale.ROOT).equals("CONNECT") -> HTTPRequestMethod.CONNECT;
            case String method when method.toUpperCase(Locale.ROOT).equals("DELETE") -> HTTPRequestMethod.DELETE;
            case String method when method.toUpperCase(Locale.ROOT).equals("GET") -> HTTPRequestMethod.GET;
            case String method when method.toUpperCase(Locale.ROOT).equals("HEAD") -> HTTPRequestMethod.HEAD;
            case String method when method.toUpperCase(Locale.ROOT).equals("OPTIONS") -> HTTPRequestMethod.OPTIONS;
            case String method when method.toUpperCase(Locale.ROOT).equals("PATCH") -> HTTPRequestMethod.PATCH;
            case String method when method.toUpperCase(Locale.ROOT).equals("POST") -> HTTPRequestMethod.POST;
            case String method when method.toUpperCase(Locale.ROOT).equals("PUT") -> HTTPRequestMethod.PUT;
            case String method when method.toUpperCase(Locale.ROOT).equals("TRACE") -> HTTPRequestMethod.TRACE;
            default -> null;
        };

        HTTPVersion httpVersion = switch (REQUEST_LINE[2]) {
            case String version when version.toUpperCase(Locale.ROOT).equals("HTTP/1.1") -> HTTPVersion.HTTP_1_1;
            default -> null;
        };


    }
}
