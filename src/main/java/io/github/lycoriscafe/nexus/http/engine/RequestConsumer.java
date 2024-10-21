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

import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.*;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class RequestConsumer implements Runnable {
    private final HttpServerConfiguration serverConfiguration;
    private final Database database;
    private final Socket socket;

    private long requestId = 0L;
    private final long responseId = 0L;

    public RequestConsumer(final HttpServerConfiguration serverConfiguration,
                           final Database database,
                           final Socket socket) {
        this.serverConfiguration = serverConfiguration;
        this.database = database;
        this.socket = socket;
    }

    private static HttpRequest getRequestInstance(final HttpServerConfiguration serverConfiguration,
                                                  final long requestId,
                                                  final String requestLine) {
        String[] parts = requestLine.split(" ");

        if (!parts[2].toUpperCase(Locale.ROOT).equals("HTTP/1.1")) {
            return null;
        }

        if (serverConfiguration.isIgnoreEndpointCases()) {
            parts[1] = parts[1].toLowerCase(Locale.ROOT);
        }

        switch (parts[0].toUpperCase(Locale.ROOT)) {
            case "GET" -> {
                return new HttpGetRequest(requestId, parts[1]);
            }
            case "POST" -> {
                return new HttpPostRequest(requestId, parts[1]);
            }
            case "PUT" -> {
                return new HttpPutRequest(requestId, parts[1]);
            }
            case "DELETE" -> {
                return new HttpDeleteRequest(requestId, parts[1]);
            }
            case "PATCH" -> {
                return new HttpPatchRequest(requestId, parts[1]);
            }
            case "HEAD" -> {
                return new HttpHeadRequest(requestId, parts[1]);
            }
            case "OPTIONS" -> {
                return new HttpOptionsRequest(requestId, parts[1]);
            }
            default -> {
                return null;
            }
        }
    }

    private static HttpRequest processHeaders(final String headerLine,
                                              final HttpRequest httpRequest) {
        String[] parts = headerLine.split(":");

        return null;
    }

    @Override
    public void run() {
        try (var reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            HttpRequest request = null;
            boolean isRequestLine = true;

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.isEmpty()) {
                    // TODO handle request
                }

                if (isRequestLine) {
                    request = getRequestInstance(serverConfiguration, requestId++, line);
                    if (request == null) {
                        // TODO handle http version error
                        break;
                    }
                    isRequestLine = false;
                    continue;
                }

                request = processHeaders(line, request);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(HttpResponse response) {

    }
}
