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

package lycoriscafe.nexus.http;

import lycoriscafe.nexus.http.configuration.MemoryType;
import lycoriscafe.nexus.http.configuration.ThreadType;
import lycoriscafe.nexus.http.connHelper.WorkerThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static lycoriscafe.nexus.http.configuration.ThreadType.PLATFORM;

public final class HTTPServer {
    private final ServerSocket SERVER_SOCKET;
    private final ThreadType THREAD_TYPE;
    private final MemoryType MEMORY_TYPE;
    private final ExecutorService EXECUTOR_SERVICE;
    private final int MAX_THREADS_PER_CONN;
    private boolean operational;

    public HTTPServer(final int PORT,
                      final ThreadType THREAD_TYPE,
                      final MemoryType MEMORY_TYPE,
                      final int MAX_CONNECTIONS,
                      final int MAX_THREADS_PER_CONN) throws IOException {
        SERVER_SOCKET = new ServerSocket(PORT);
        this.THREAD_TYPE = THREAD_TYPE;
        this.MEMORY_TYPE = MEMORY_TYPE;
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        this.MAX_THREADS_PER_CONN = MAX_THREADS_PER_CONN;
    }

    public HTTPServer(final int PORT,
                      final int BACKLOG,
                      final ThreadType THREAD_TYPE,
                      final MemoryType MEMORY_TYPE,
                      final int MAX_CONNECTIONS,
                      final int MAX_THREADS_PER_CONN) throws IOException {
        SERVER_SOCKET = new ServerSocket(PORT, BACKLOG);
        this.THREAD_TYPE = THREAD_TYPE;
        this.MEMORY_TYPE = MEMORY_TYPE;
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        this.MAX_THREADS_PER_CONN = MAX_THREADS_PER_CONN;
    }

    public HTTPServer(final int PORT,
                      final int BACKLOG,
                      final InetAddress ADDRESS,
                      final ThreadType THREAD_TYPE,
                      final MemoryType MEMORY_TYPE,
                      final int MAX_CONNECTIONS,
                      final int MAX_THREADS_PER_CONN) throws IOException {
        SERVER_SOCKET = new ServerSocket(PORT, BACKLOG, ADDRESS);
        this.THREAD_TYPE = THREAD_TYPE;
        this.MEMORY_TYPE = MEMORY_TYPE;
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        this.MAX_THREADS_PER_CONN = MAX_THREADS_PER_CONN;
    }

    public void start() throws IllegalStateException {
        if (!operational) {
            Thread.Builder engine = THREAD_TYPE == PLATFORM ? Thread.ofPlatform() : Thread.ofVirtual();
            engine.start(() -> {
                operational = true;
                while (operational) {
                    try {
                        Thread.Builder worker = THREAD_TYPE == PLATFORM ? Thread.ofPlatform() : Thread.ofVirtual();
                        EXECUTOR_SERVICE.submit(worker.start(
                                new WorkerThread(SERVER_SOCKET.accept(), THREAD_TYPE, MAX_THREADS_PER_CONN)));
                    } catch (IOException e) {
                        operational = false;
                    }
                }
            });
        } else {
            throw new IllegalStateException("Server is already running!");
        }
    }

    public void stop() throws IOException, IllegalStateException {
        if (operational) {
            operational = false;
            SERVER_SOCKET.close();
            EXECUTOR_SERVICE.shutdownNow();
        } else {
            throw new IllegalStateException("Server is already stopped!");
        }
    }
}
