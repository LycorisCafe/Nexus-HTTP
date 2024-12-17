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

import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class RequestConsumer implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(RequestConsumer.class);

    private static final byte[] lineTerminator = "\r\n".getBytes(StandardCharsets.UTF_8);
    private final RequestProcessor requestProcessor;

    private final HttpServerConfiguration serverConfiguration;
    private final Database database;
    private final Socket socket;

    // readLine() components
    private final byte[] terminatePoint = new byte[2];
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private final SortedMap<Long, HttpResponse> responseQue;
    private long requestId = 0L;
    private long responseId = 0L;

    public RequestConsumer(final HttpServerConfiguration serverConfiguration,
                           final Database database,
                           final Socket socket) throws IOException {
        requestProcessor = new RequestProcessor(this);

        this.serverConfiguration = Objects.requireNonNull(serverConfiguration);
        this.database = Objects.requireNonNull(database);
        this.socket = Objects.requireNonNull(socket);

        this.socket.setSoTimeout(serverConfiguration.getConnectionTimeout());
        responseQue = new TreeMap<>();
    }

    private long getRequestId() {
        if (requestId == Long.MAX_VALUE) {
            try {
                wait();
                return -1L;
            } catch (InterruptedException e) {
                // just wait for drop the connection
            }
        }
        if (requestId + 2 == Long.MAX_VALUE) {
            dropConnection(Long.MAX_VALUE, HttpStatusCode.INTERNAL_SERVER_ERROR, "connection reset");
        }
        return requestId++;
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

    public String readLine() throws IOException {
        byteArrayOutputStream.reset();

        int c = socket.getInputStream().read(terminatePoint, 0, 2);
        if (c != 2) return null;

        while (!Arrays.equals(lineTerminator, terminatePoint)) {
            int b = socket.getInputStream().read();
            if (b == -1) return null;
            byteArrayOutputStream.write(terminatePoint[0]);
            terminatePoint[0] = terminatePoint[1];
            terminatePoint[1] = (byte) b;
        }

        return byteArrayOutputStream.size() == 0 ? "" : byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        logger.atTrace().log("client connection received");
        Thread.currentThread().setName("RequestConsumer");
        try {
            String requestLine = null;
            List<String> headers = new ArrayList<>();

            while (true) {
                String line = readLine();
                if (line == null) break;
                line = line.trim();

                if (requestLine == null) {
                    requestLine = line;
                    continue;
                }

                if (line.isEmpty()) {
                    requestProcessor.process(getRequestId(), requestLine, headers);
                    requestLine = null;
                    headers.clear();
                    continue;
                }

                if (headers.size() > serverConfiguration.getMaxHeadersPerRequest()) {
                    // Handle max headers count exceeded
                    dropConnection(requestId, HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "request header fields too large");
                    return;
                }

                headers.add(line);
            }
        } catch (IOException e) {
            logger.atDebug().log(e.getMessage());
        }
        logger.atTrace().log("client connection terminated");
    }

    public void dropConnection(final long requestId,
                               final HttpStatusCode httpStatusCode,
                               final String errorMessage) {
        logger.atDebug().log("connection drop requested : id " + requestId + "; cause " + httpStatusCode + " " + errorMessage);
        HttpResponse httpResponse = new HttpResponse(requestId, this, httpStatusCode).setDropConnection(true);
        if (getServerConfiguration().isAddErrorMessageToResponseHeaders() && errorMessage != null) {
            httpResponse.setContent(new Content("application/json", "{\"errorMessage\":\"" + errorMessage + "\"}"));
        }
        send(httpResponse);
    }

    public synchronized void send(final HttpResponse httpResponse) {
        if (socket.isClosed()) {
            return;
        }
        responseQue.put(httpResponse.getRequestId(), httpResponse);

        List<Long> keySet = responseQue.keySet().stream().toList();
        for (Long key : keySet) {
            if (key != responseId) {
                break;
            }

            HttpResponse response = responseQue.get(key);
            if (response.getRequestId() == responseId) {
                try {
                    OutputStream outputStream = socket.getOutputStream();

                    String headers = response.finalizeResponse();
                    if (headers == null) return;
                    outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();

                    if (response.getContent() != null) {
                        Content.WriteOperations.writeContent(this, response.getContent());
                    }

                    if (response.isDropConnection()) {
                        socket.close();
                        notify();
                    }
                } catch (IOException e) {
                    logger.atDebug().log(e.getMessage());
                }

                responseQue.remove(key);
                responseId++;
            }
        }
    }
}
