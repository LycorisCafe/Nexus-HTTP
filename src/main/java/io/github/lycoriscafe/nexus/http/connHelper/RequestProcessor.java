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
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.RequestMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
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
                 final Map<String, List<String>> HEADERS)
            throws SQLException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        final HTTPRequest REQUEST = switch (REQUEST_LINE[0]) {
            case String req when req.toLowerCase(Locale.ROOT).equals("get") ->
                    new HTTPRequest(REQUEST_ID, RequestMethod.GET, HEADERS);
            case String req when req.toLowerCase(Locale.ROOT).equals("post") ->
                    new HTTPRequest(REQUEST_ID, RequestMethod.POST, HEADERS);
            // TODO implement other http methods
            default -> {
                if (executorService != null) {
                    executorService.shutdownNow();
                }
                CONN_HANDLER.closeSocket("Unexpected HTTP Header! Closing connection.");
                yield null;
            }
        };

        if (REQUEST == null) {
            return;
        }

        HTTPResponse<?> tempResponse = new HTTPResponse<>(REQUEST_ID);
        String[] locations = ClassFinder.findGet(
                DATABASE, "REQ" + REQUEST.getMethod().toString(), REQUEST_LINE[1]);

        Class<?> endpointClass = Class.forName(locations[0]);
        Method endpointMethod = endpointClass.getMethod(
                locations[1], HTTPRequest.class, HTTPResponse.class);
        HTTPResponse<?> RESPONSE = (HTTPResponse<?>) endpointMethod.invoke(null, REQUEST, tempResponse);

// TODO complete the code
    }
}
