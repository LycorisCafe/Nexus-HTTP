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

package lycoriscafe.nexus.http.connHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorkerThread implements Runnable {
    private final Socket SOCKET;
    private final ExecutorService EXECUTOR;

    public WorkerThread(final Socket SOCKET, final int MAX_PARALLEL_THREADS) throws IOException {
        this.SOCKET = SOCKET;
        EXECUTOR = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        final ArrayList<String> headers = new ArrayList<>();
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
                                break headersLoop;
                            }
                            continue;
                        }
                        terminateCount = 0;
                        headers.add(line);
                        buffer = new ByteArrayOutputStream();
                    }
                    default -> buffer.write(character);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(headers);
    }

    public synchronized void send(final byte[] data) {
        try {
            SOCKET.getOutputStream().write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
