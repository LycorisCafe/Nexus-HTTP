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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

public final class ConnectionHandler implements Runnable {
    private final Socket SOCKET;
    private final Connection DATABASE;

    public ConnectionHandler(final Socket SOCKET, final Connection DATABASE)
            throws IOException {
        this.SOCKET = SOCKET;
        this.DATABASE = DATABASE;
    }

    @Override
    public void run() {
        final ArrayList<String> HEADERS = new ArrayList<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int terminateCount = 0;

        headersLoop:
        while (true) {
            try {
                int character = SOCKET.getInputStream().read();
                switch (character) {
                    case -1 -> {
                        break headersLoop;
                    }
                    case '\r', '\n' -> {
                        terminateCount++;
                        String line = buffer.toString(StandardCharsets.UTF_8);
                        if (line.isEmpty()) {
                            if (terminateCount == 3) {
                                switch (HEADERS.getFirst().split(" ")[0].toLowerCase(Locale.ROOT)) {
                                    case "get" -> {

                                    }
                                }
                            }
                            continue;
                        }
                        terminateCount = 0;
                        HEADERS.add(line);
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
            SOCKET.getOutputStream().write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
