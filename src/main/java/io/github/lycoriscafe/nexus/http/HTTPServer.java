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

import io.github.lycoriscafe.nexus.http.configuration.AnnotationScanner;
import io.github.lycoriscafe.nexus.http.configuration.Database;
import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.connHelper.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static io.github.lycoriscafe.nexus.http.configuration.ThreadType.PLATFORM;

public final class HTTPServer {
    private final HTTPServerConfiguration CONFIGURATION;
    private final ServerSocket SERVER_SOCKET;
    private final Connection DATABASE;

    private ExecutorService executorService;
    private boolean operational;

    public HTTPServer(final HTTPServerConfiguration httpServerConfiguration)
            throws IOException, IllegalArgumentException, SQLException {
        if (httpServerConfiguration == null) {
            throw new IllegalArgumentException("HTTP Server configuration cannot be null");
        }

        CONFIGURATION = httpServerConfiguration;
        DATABASE = Database.getConnection(httpServerConfiguration.getDatabaseLocation(),
                httpServerConfiguration.getPort());
        AnnotationScanner.scan(DATABASE, httpServerConfiguration.getBasePackage());
        SERVER_SOCKET = httpServerConfiguration.getAddress() == null ?
                new ServerSocket(httpServerConfiguration.getPort(), httpServerConfiguration.getBacklog()) :
                new ServerSocket(httpServerConfiguration.getPort(), httpServerConfiguration.getBacklog(),
                        httpServerConfiguration.getAddress());
    }

    public void start() throws IllegalStateException {
        if (!operational) {
            Thread.ofPlatform().start(() -> {
                operational = true;
                ThreadFactory worker = CONFIGURATION.getThreadType() == PLATFORM ?
                        Thread.ofPlatform().factory() : Thread.ofVirtual().factory();
                executorService = Executors.newFixedThreadPool(CONFIGURATION.getMaxConnections(), worker);
                while (operational) {
                    try {
                        executorService.execute(new ConnectionHandler(CONFIGURATION, SERVER_SOCKET.accept(), DATABASE));
                    } catch (IOException e) {
                        operational = false;
                    }
                }
                executorService.shutdownNow();
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
