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
import io.github.lycoriscafe.nexus.http.helper.util.LogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Base class for initialize a server.
 *
 * @since v1.0.0
 */
public final class HttpServer {
    private final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final HttpServerConfiguration serverConfiguration;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final Database database;

    /**
     * Create instance for an HTTP server.
     *
     * @param httpServerConfiguration {@code HttpServerConfiguration} instance
     * @throws SQLException     Error while initializing database connection
     * @throws IOException      Error while reading/writing directories
     * @throws ScannerException Error while scanning for endpoints
     * @see HttpServerConfiguration
     * @see HttpServer
     * @since v1.0.0
     */
    public HttpServer(final HttpServerConfiguration httpServerConfiguration) throws SQLException, IOException, ScannerException {
        serverConfiguration = Objects.requireNonNull(httpServerConfiguration);

        Path tempPath = Paths.get(httpServerConfiguration.getTempDirectory());
        if (!Files.exists(tempPath)) Files.createDirectory(tempPath);

        if (httpServerConfiguration.getStaticFilesDirectory() != null) {
            Path staticPath = Paths.get(httpServerConfiguration.getStaticFilesDirectory());
            if (!Files.exists(staticPath) || !Files.isDirectory(staticPath)) throw new IllegalStateException("static path cannot be found");
        }

        database = new Database(serverConfiguration);
        EndpointScanner.scan(serverConfiguration, database);
        FileScanner.scan(serverConfiguration, database);
    }

    /**
     * Initialize executor service for client connections.
     *
     * @param serverConfiguration {@code HttpServerConfiguration} instance bound to the server
     * @return {@code ExecutorService} with fixed thread-pool
     * @see HttpServerConfiguration
     * @see ExecutorService
     * @see HttpServer
     * @since v1.0.0
     */
    private static ExecutorService initializeExecutorService(final HttpServerConfiguration serverConfiguration) {
        return Executors.newFixedThreadPool(serverConfiguration.getMaxIncomingConnections(), serverConfiguration.getThreadType() == ThreadType.PLATFORM ?
                Thread.ofPlatform().factory() : Thread.ofVirtual().factory());
    }

    /**
     * Start HTTP server with given {@code HttpServerConfiguration} settings. After initialization, a log wll print with the listening network
     * interface {@code address} and {@code port} (interface address {@code 0.0.0.0} simply means server listening to all network interfaces).
     *
     * @see HttpServerConfiguration
     * @see #shutdown()
     * @see HttpServer
     * @since v1.0.0
     */
    public synchronized HttpServer initialize() throws InterruptedException, IOException {
        if (serverThread != null && serverThread.isAlive()) throw new IllegalStateException("http server already running");

        // Simple decoration
        LogFormatter.log(logger.atInfo(), "_____ _____ __ __ _____ _____");
        LogFormatter.log(logger.atInfo(), "|   | |   __|  |  |  |  |   __|");
        LogFormatter.log(logger.atInfo(), "| | | |   __|-   -|  |  |__   |");
        LogFormatter.log(logger.atInfo(), "|_|___|_____|__|__|_____|_____| HTTP v1.0");

        executorService = initializeExecutorService(serverConfiguration);
        serverSocket = serverConfiguration.getInetAddress() == null ?
                new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog()) :
                new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog(), serverConfiguration.getInetAddress());
        serverThread = Thread.ofPlatform().start(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    executorService.execute(new RequestConsumer(serverConfiguration, database, serverSocket.accept()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        LogFormatter.log(logger.atDebug(), "Server initialized @ " + serverSocket.getLocalSocketAddress());
        return this;
    }

    /**
     * Shutdown HTTP server.
     *
     * @throws IOException          Error while stop the server
     * @throws InterruptedException Executor services related exceptions
     * @see #initialize()
     * @see HttpServer
     * @since v1.0.0
     */
    public synchronized void shutdown() throws IOException, InterruptedException {
        if (!serverThread.isAlive()) throw new IllegalStateException("http server already shutdown");
        if (!executorService.awaitTermination(serverConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS)) executorService.shutdownNow();
        serverSocket.close();
        if (serverThread.isAlive()) serverThread.interrupt();
    }
}
