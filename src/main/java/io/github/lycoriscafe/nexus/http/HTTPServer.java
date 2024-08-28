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

package io.github.lycoriscafe.nexus.http;

import io.github.lycoriscafe.nexus.http.configuration.Database;
import io.github.lycoriscafe.nexus.http.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.connHelper.ConnectionHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.lycoriscafe.nexus.http.configuration.ThreadType.PLATFORM;

public final class HTTPServer {
    private final ServerSocket SERVER_SOCKET;
    private final ThreadType THREAD_TYPE;
    private final Connection DATABASE;
    private final int MAX_CONNECTIONS;

    private ExecutorService executorService;
    private boolean operational;

    public HTTPServer(final int PORT,
                      final ThreadType THREAD_TYPE,
                      final String DB_LOCATION,
                      final int MAX_CONNECTIONS)
            throws IOException, IllegalArgumentException, SQLException {
        maxConnValidator(MAX_CONNECTIONS);
        DATABASE = Database.getConnection(DB_LOCATION);
        SERVER_SOCKET = new ServerSocket(PORT);
        this.THREAD_TYPE = THREAD_TYPE;
        this.MAX_CONNECTIONS = MAX_CONNECTIONS;
    }

    public HTTPServer(final int PORT,
                      final int BACKLOG,
                      final ThreadType THREAD_TYPE,
                      final String DB_LOCATION,
                      final int MAX_CONNECTIONS)
            throws IOException, IllegalArgumentException, SQLException {
        maxConnValidator(MAX_CONNECTIONS);
        DATABASE = Database.getConnection(DB_LOCATION);
        SERVER_SOCKET = new ServerSocket(PORT, BACKLOG);
        this.THREAD_TYPE = THREAD_TYPE;
        this.MAX_CONNECTIONS = MAX_CONNECTIONS;
    }

    public HTTPServer(final int PORT,
                      final int BACKLOG,
                      final InetAddress ADDRESS,
                      final ThreadType THREAD_TYPE,
                      final String DB_LOCATION,
                      final int MAX_CONNECTIONS)
            throws IOException, IllegalArgumentException, SQLException {
        maxConnValidator(MAX_CONNECTIONS);
        DATABASE = Database.getConnection(DB_LOCATION);
        SERVER_SOCKET = new ServerSocket(PORT, BACKLOG, ADDRESS);
        this.THREAD_TYPE = THREAD_TYPE;
        this.MAX_CONNECTIONS = MAX_CONNECTIONS;
    }

    private void maxConnValidator(int maxConn) throws IllegalArgumentException {
        if (maxConn < 1) {
            throw new IllegalArgumentException("Max connections must be greater than 0");
        }
    }

    public void start() throws IllegalStateException {
        System.out.println(operational);
        if (!operational) {
            Thread.Builder engine = THREAD_TYPE == PLATFORM ? Thread.ofPlatform() : Thread.ofVirtual();
            engine.start(() -> {
                operational = true;
                Thread.Builder worker = THREAD_TYPE == PLATFORM ? Thread.ofPlatform() : Thread.ofVirtual();
                executorService = Executors.newFixedThreadPool(MAX_CONNECTIONS, worker.factory());
                while (operational) {
                    try {
                        executorService.submit(new ConnectionHandler(SERVER_SOCKET.accept()));
                    } catch (IOException e) {
                        operational = false;
                        executorService.shutdownNow();
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
        } else {
            throw new IllegalStateException("Server is already stopped!");
        }
    }
}
