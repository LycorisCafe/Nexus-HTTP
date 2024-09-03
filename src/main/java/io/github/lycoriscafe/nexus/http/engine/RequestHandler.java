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
import io.github.lycoriscafe.nexus.http.core.HTTPVersion;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HTTPRequestMethod;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void incrementRequestId() {
        requestId = (responseId == Long.MAX_VALUE ? 0 : responseId + 1);
    }

    private void incrementResponseId() {
        responseId = (responseId == Long.MAX_VALUE ? 0 : responseId + 1);
    }

    private ArrayList<Object> validateRequestLine(final String requestLine) {
        try {
            HTTPVersion httpVersion;
            String[] parts = requestLine.split(" ");
            if (parts.length != 3 ||
                    !HTTPRequestMethod.validate(parts[0]) ||
                    (httpVersion = HTTPVersion.validate(parts[2])) == null) {
                return null;
            }

            ArrayList<Object> request = new ArrayList<>();
            request.add(HTTPRequestMethod.valueOf(parts[0]));
            request.add(parts[1]);
            request.add(httpVersion);
            return request;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, List<String>> processHeader(final String headerLine) {
        Map<String, List<String>> header = new HashMap<>();
        List<String> values = new ArrayList<>();
        try {
            String[] parts = headerLine.splitWithDelimiters(":", 2);
            for (String value : parts[2].split(",")) {
                if (value.charAt(0) == ' ') {
                    value = value.replaceFirst(" ", "");
                }
                values.add(value);
            }
            header.put(parts[0], values);
            return header;
        } catch (Exception e) {
            return null;
        }
    }

    public void addToSendQue(final HTTPResponse<?> httpResponse) {
        RESPONSES.add(httpResponse);
        send();
    }

    @Override
    public void run() {
        ArrayList<Object> requestLine = null;
        Map<String, List<String>> headers = new HashMap<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int terminator = 0;
        int character;

        headersLoop:
        while (true) {
            try {
                character = INPUT_STREAM.read();
                switch (character) {
                    case -1 -> {
                        break headersLoop;
                    }
                    case '\r' -> {
                        INPUT_STREAM.skipNBytes(1);

                        terminator++;
                        if (terminator == 2) {
                            System.out.println(requestLine);
                            System.out.println(headers);
                            // TODO send to process
                            requestLine = null;
                            headers.clear();
                            terminator = 0;
                            continue;
                        }

                        if (requestLine == null) {
                            requestLine = validateRequestLine(buffer.toString(StandardCharsets.UTF_8));
                            if (requestLine == null) {
                                System.out.println("400 - 1");
                                // TODO send error message (400 BAD REQUEST)
                                break headersLoop;
                            }
                            buffer.reset();
                            continue;
                        }

                        String headerLine = buffer.toString(StandardCharsets.UTF_8);
                        Map<String, List<String>> tempHeader = processHeader(headerLine);
                        if (tempHeader == null) {
                            System.out.println("400 - 2");
                            // TODO send error message (400 BAD REQUEST)
                            break headersLoop;
                        }
                        headers.putAll(tempHeader);
                        buffer.reset();
                    }
                    default -> {
                        buffer.write(character);
                        terminator = 0;
                    }
                }
            } catch (IOException e) {
                // TODO handle socket io exception
            }
        }
    }

    private synchronized void send() {
        for (HTTPResponse<?> httpResponse : RESPONSES) {
            if (httpResponse.getResponseId() == responseId) {
                // TODO implement send
                incrementResponseId();
            }
        }
    }
}
