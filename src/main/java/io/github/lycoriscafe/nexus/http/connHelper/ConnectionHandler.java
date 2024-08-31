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

public final class ConnectionHandler implements Runnable {
    private final BufferedInputStream INPUT_STREAM;
    private final BufferedOutputStream OUTPUT_STREAM;
    private final RequestProcessor PROCESSOR;

    public ConnectionHandler(final HTTPServerConfiguration CONFIGURATION,
                             final Socket SOCKET,
                             final Connection DATABASE) throws IOException {
        this.INPUT_STREAM = new BufferedInputStream(SOCKET.getInputStream());
        this.OUTPUT_STREAM = new BufferedOutputStream(SOCKET.getOutputStream());
        PROCESSOR = new RequestProcessor(CONFIGURATION, INPUT_STREAM, DATABASE);
    }

    @Override
    public void run() {
        String requestLine = null;
        boolean isRequestLine = true;
        Map<String, List<String>> headers = new HashMap<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int terminateCount = 0;

        headersLoop:
        while (true) {
            try {
                int character = INPUT_STREAM.read();
                switch (character) {
                    case -1 -> {
                        break headersLoop;
                    }
                    case '\r', '\n' -> {
                        terminateCount++;
                        String line = buffer.toString(StandardCharsets.UTF_8);
                        if (line.isEmpty()) {
                            if (terminateCount == 3) {
                                System.out.println(headers);
                                PROCESSOR.process(requestLine, headers);
                                headers = new HashMap<>();
                            }
                            continue;
                        }
                        terminateCount = 0;

                        if (isRequestLine) {
                            requestLine = line;
                            isRequestLine = false;
                        } else {
                            String headerName = line.split(":")[0];
                            ArrayList<String> values = new ArrayList<>();
                            for (String value : line.replace(headerName + ":", "").split(",")) {
                                values.add(value.charAt(0) == ' ' ? value.replaceFirst(" ", "") : value);
                            }
                            headers.put(headerName, values);
                        }

                        buffer = new ByteArrayOutputStream();
                    }
                    default -> buffer.write(character);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void send(final byte[] data) {
        try {
            OUTPUT_STREAM.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
