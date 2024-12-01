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

import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class RequestConsumer implements Runnable {
    private final RequestProcessor requestProcessor;

    private final HttpServerConfiguration serverConfiguration;
    private final Database database;
    private final Socket socket;

    private final BufferedReader reader;

    private long requestId = 0L;
    private long responseId = 0L;

    public RequestConsumer(final HttpServerConfiguration serverConfiguration,
                           final Database database,
                           final Socket socket) throws IOException {
        requestProcessor = new RequestProcessor(this);

        this.serverConfiguration = serverConfiguration;
        this.database = database;
        this.socket = socket;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    public HttpServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public Database getDatabase() {
        return database;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getReader() {
        return reader;
    }

    @Override
    public void run() {
        try {
            String requestLine = null;
            List<String> headers = new ArrayList<>();

            while (true) {
                String line = reader.readLine().trim();
                if (line.length() > 8000) {
                    // Handle length exceeded
                    dropConnection(requestId, HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
                    return;
                }

                if (requestLine == null) {
                    requestLine = line;
                    continue;
                }

                if (line.isEmpty()) {
                    requestProcessor.process(requestId++, requestLine, headers);
                    requestLine = null;
                    headers.clear();
                    continue;
                }

                if (headers.size() > serverConfiguration.getMaxHeadersPerRequest()) {
                    // Handle max headers count exceeded
                    dropConnection(requestId, HttpStatusCode.BAD_REQUEST);
                    return;
                }

                headers.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropConnection(final long requestId,
                               final HttpStatusCode httpStatusCode) {
        send(new HttpResponse(requestId, this, httpStatusCode));
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<HttpResponse> responseQue = new ArrayList<>();

    public void send(final HttpResponse httpResponse) {
        if (socket.isClosed()) {
            return;
        }
        responseQue.add(httpResponse);

        for (HttpResponse response : responseQue) {
            if (response.getResponseId() == responseId) {
                try {
                    OutputStream outputStream = socket.getOutputStream();

                    String headers = response.finalizeResponse();
                    if (headers == null) return;
                    outputStream.write(headers.getBytes(StandardCharsets.UTF_8));

                    if (response.getContent() != null) {
                        // TODO implement
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                responseQue.remove(response);
                responseId++;
            }
        }
    }
}
