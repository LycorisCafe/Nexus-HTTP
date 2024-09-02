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
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.*;

public final class RequestHandler implements Runnable {
    Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    final List<HTTPResponse<?>> RESPONSES = new ArrayList<>();

    private long requestId = 0;
    private long responseId = 0;

    private final Socket SOCKET;
    private final BufferedInputStream INPUT_STREAM;
    private final BufferedOutputStream OUTPUT_STREAM;
    private final RequestProcessor PROCESSOR;

    public RequestHandler(final HTTPServerConfiguration CONFIGURATION,
                          final Socket SOCKET,
                          final Connection DATABASE) throws IOException {
        this.SOCKET = SOCKET;
        this.INPUT_STREAM = new BufferedInputStream(SOCKET.getInputStream());
        this.OUTPUT_STREAM = new BufferedOutputStream(SOCKET.getOutputStream());
        PROCESSOR = new RequestProcessor(this, CONFIGURATION, DATABASE);
    }

    void closeSocket(String errorMessage) {
        try {
            SOCKET.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedInputStream getInputStream() {
        return INPUT_STREAM;
    }

    private long getRequestId() {
        return requestId++;
    }

    public void addToSendQue(final HTTPResponse<?> httpResponse) {
        RESPONSES.add(httpResponse);
        send();
    }

    @Override
    public void run() {
        String requestLine = null;
        Map<String, List<String>> headers = new HashMap<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int terminateCount = 0;
        int character;

        headersLoop:
        while (true) {
            try {
                character = INPUT_STREAM.read();
                switch (character) {
                    case -1 -> {
                        break headersLoop;
                    }
                    case '\r', '\n' -> {
                        terminateCount++;
                        String line = buffer.toString(StandardCharsets.UTF_8);
                        if (line.isEmpty()) {
                            if (terminateCount == 3) {
                                System.out.println(requestLine);
                                System.out.println(headers);
                                PROCESSOR.process(getRequestId(), requestLine.split(" "), headers);
                                requestLine = null;
                                headers = new HashMap<>();
                            }
                            continue;
                        }
                        terminateCount = 0;

                        if (requestLine == null) {
                            requestLine = line;
                        } else {
                            String headerName = line.split(":")[0];
                            ArrayList<String> values = new ArrayList<>();
                            for (String value : line.replace(headerName + ":", "").split(",")) {
                                values.add(value.charAt(0) == ' ' ? value.replaceFirst(" ", "") : value);
                            }
                            headers.put(headerName.toUpperCase(Locale.ROOT), values);
                        }

                        buffer = new ByteArrayOutputStream();
                    }
                    default -> buffer.write(character);
                }
            } catch (IOException e) {
                closeSocket(e.getMessage());
            }
        }
    }

    private synchronized void send() {
        for (HTTPResponse<?> httpResponse : RESPONSES) {
            if (httpResponse.getResponseId() == responseId) {
                // TODO implement send
                responseId++;
            }
        }
    }
}
