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

import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.configuration.ThreadType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HttpServer {
    private final HttpServerConfiguration serverConfiguration;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Connection databaseConnection;

    public HttpServer(final HttpServerConfiguration httpServerConfiguration) throws SQLException, IOException {
        serverConfiguration = httpServerConfiguration;
        executorService = initializeExecutorService(serverConfiguration);
        databaseConnection = Database.initializeDatabaseConnection(serverConfiguration);
    }

    private static ExecutorService initializeExecutorService(final HttpServerConfiguration serverConfiguration) {
        return Executors.newFixedThreadPool(serverConfiguration.getMaxIncomingConnections(),
                serverConfiguration.getThreadType() == ThreadType.PLATFORM ?
                        Thread.ofPlatform().factory() : Thread.ofVirtual().factory());
    }

    public void initialize() {
        serverThread = Thread.ofPlatform().start(() -> {
            try {
                serverSocket = serverConfiguration.getInetAddress() == null ?
                        new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog()) :
                        new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog(),
                                serverConfiguration.getInetAddress());

                while (true) {
                    // TODO implement using executorService
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(60_000);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void shutdown() throws IOException, InterruptedException {
        executorService.shutdown();
        if (executorService.isShutdown()) {
            serverSocket.close();
            Thread.sleep(1000);
            if (serverThread.isAlive()) {
                serverThread.interrupt();
            }
        }
    }
}
