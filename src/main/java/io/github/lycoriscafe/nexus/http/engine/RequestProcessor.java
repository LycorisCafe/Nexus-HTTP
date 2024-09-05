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
import io.github.lycoriscafe.nexus.http.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.core.HTTPVersion;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.sql.Connection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RequestProcessor {
    private ExecutorService executorService;
    private final RequestHandler REQ_HANDLER;
    private final BufferedInputStream INPUT_STREAM;
    private final Connection DATABASE;
    private final long MAX_CONTENT_LENGTH;
    private final File TEMP_DIR;

    RequestProcessor(final RequestHandler REQ_HANDLER,
                     final BufferedInputStream INPUT_STREAM,
                     final HTTPServerConfiguration CONFIGURATION,
                     final Connection DATABASE) {
        this.REQ_HANDLER = REQ_HANDLER;
        this.INPUT_STREAM = INPUT_STREAM;
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
                 final ArrayList<Object> REQUEST_LINE,
                 final Map<String, List<String>> HEADERS) {

    }

    void processError(final long REQUEST_ID,
                      final HTTPStatusCode STATUS) {
        HTTPResponse<?> response = new HTTPResponse<>(REQUEST_ID);
        response.setVersion(HTTPVersion.HTTP_1_1);
        response.setStatusCode(STATUS);
        addDefaultHeaders(response);
        response.formatProtocol();
        REQ_HANDLER.addToSendQue(response);
    }

    private static void addDefaultHeaders(final HTTPResponse<?> RESPONSE) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Server", List.of("LycorisCafe/NexusHTTP(v1.0.0)"));
        headers.put("Date", List.of(getServerTime()));
        RESPONSE.setHeaders(headers);
    }

    private static String getServerTime() {
        return DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                .withZone(ZoneId.of("GMT")).format(ZonedDateTime.now());
    }
}
