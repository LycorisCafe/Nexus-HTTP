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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerThread implements Runnable {
    private final Socket SOCKET;
    private final InputStream READER;
    private final OutputStream WRITER;
    private final ExecutorService EXECUTOR;

    public WorkerThread(final Socket socket) throws IOException {
        SOCKET = socket;
        READER = SOCKET.getInputStream();
        WRITER = SOCKET.getOutputStream();
        EXECUTOR = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        final ArrayList<String> headers = new ArrayList<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] b = new byte[1];

        headersLoop:
        while (true) {
            try {
                int character = READER.read(b);
                switch (character) {
                    case -1 -> {
                        break headersLoop;
                    }
                    case '\r', '\n' -> {
                        String line = buffer.toString(StandardCharsets.UTF_8);
                        if (line.isEmpty()) {
                            continue;
                        }
                        headers.add(line);
                        buffer = new ByteArrayOutputStream();
                    }
                    default -> buffer.write(b);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        headers.add(buffer.toString());
        System.out.println(headers);
    }

    public synchronized void send(final byte[] data) {
        try {
            WRITER.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
