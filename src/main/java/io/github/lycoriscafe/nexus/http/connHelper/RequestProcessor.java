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

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RequestProcessor {
    private int requestId;
    private ExecutorService executorService;
    private final ConnectionHandler HANDLER;
    private final Connection DATABASE;

    RequestProcessor(final ConnectionHandler HANDLER,
                     final HTTPServerConfiguration CONFIGURATION,
                     final Connection DATABASE) {
        this.HANDLER = HANDLER;
        this.DATABASE = DATABASE;

        if (CONFIGURATION.isHttpPipelined()) {
            executorService = Executors.newFixedThreadPool(CONFIGURATION.getHttpPipelineParallelCount(),
                    (CONFIGURATION.getThreadType() == ThreadType.PLATFORM ?
                            Thread.ofPlatform().factory() : Thread.ofVirtual().factory()));
        }
    }

    void process(final int REQUEST_ID,
                 final String[] REQUEST,
                 final Map<String, List<String>> HEADERS) {

    }

    private int getRequestId() {
        return requestId++;
    }
}
