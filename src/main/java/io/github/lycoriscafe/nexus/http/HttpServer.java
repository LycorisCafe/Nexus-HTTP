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

import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.helper.scanners.EndpointScanner;
import io.github.lycoriscafe.nexus.http.helper.scanners.FileScanner;
import io.github.lycoriscafe.nexus.http.helper.scanners.ScannerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class HttpServer {
    private final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final HttpServerConfiguration serverConfiguration;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final Database database;

    public HttpServer(final HttpServerConfiguration httpServerConfiguration) throws SQLException, IOException, ScannerException {
        Objects.requireNonNull(httpServerConfiguration);

        serverConfiguration = httpServerConfiguration;
        database = new Database(serverConfiguration);

        EndpointScanner.scan(serverConfiguration, database);
        FileScanner.scan(serverConfiguration, database);
    }

    private static ExecutorService initializeExecutorService(final HttpServerConfiguration serverConfiguration) {
        return Executors.newFixedThreadPool(serverConfiguration.getMaxIncomingConnections(), serverConfiguration.getThreadType() == ThreadType.PLATFORM ?
                Thread.ofPlatform().factory() : Thread.ofVirtual().factory());
    }

    public void initialize() throws HttpServerException {
        if (serverThread != null && serverThread.isAlive()) {
            throw new HttpServerException("http server already running");
        }

        logger.atTrace().log("http server initializing...");
        executorService = initializeExecutorService(serverConfiguration);
        serverThread = Thread.ofPlatform().start(() -> {
            try {
                serverSocket = serverConfiguration.getInetAddress() == null ?
                        new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog()) :
                        new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog(), serverConfiguration.getInetAddress());
                logger.atTrace().log("http server initialized!");

                while (!serverSocket.isClosed()) {
                    executorService.execute(new RequestConsumer(serverConfiguration, database, serverSocket.accept()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void shutdown() throws IOException, InterruptedException, HttpServerException {
        if (!serverThread.isAlive()) {
            throw new HttpServerException("http server already shutdown");
        }

        logger.atTrace().log("http server shutting down...");
        if (!executorService.awaitTermination(serverConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
        }
        serverSocket.close();
        if (serverThread.isAlive()) {
            serverThread.interrupt();
        }
        logger.atTrace().log("http server shutdown!");
    }
}
