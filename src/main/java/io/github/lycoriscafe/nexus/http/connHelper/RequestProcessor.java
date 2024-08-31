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
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.headers.HeadersProcessor;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RequestProcessor {
    private int requestId;
    private final HTTPServerConfiguration CONFIGURATION;
    private final BufferedInputStream INPUT_STREAM;
    private final Connection DATABASE;

    RequestProcessor(final HTTPServerConfiguration CONFIGURATION,
                     final BufferedInputStream INPUT_STREAM,
                     final Connection DATABASE) {
        this.CONFIGURATION = CONFIGURATION;
        this.INPUT_STREAM = INPUT_STREAM;
        this.DATABASE = DATABASE;
    }

    void process(final String REQUEST,
                 final Map<String, List<String>> HEADERS) {

    }

    private HTTPResponse process(final HTTPRequest REQUEST) {
        HTTPResponse httpResponse = new HTTPResponse(REQUEST.getRequestID());

        for (Map.Entry<String, ArrayList<String>> entry : REQUEST.getHeaders().entrySet()) {
            httpResponse = switch (entry.getKey()) {
                case "accept" -> HeadersProcessor.accept(httpResponse, entry.getValue());
                default -> httpResponse;
            };
        }

        return httpResponse;
    }
}
