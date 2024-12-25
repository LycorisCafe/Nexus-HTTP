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
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpsServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.scanners.ScannerException;
import io.github.lycoriscafe.nexus.http.helper.util.LogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.SQLException;

/**
 * Base class for initialize an HTTPS server.
 *
 * @see HttpServer
 * @since v1.0.0
 */
public final class HttpsServer extends HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpsServer.class);
    private final HttpsServerConfiguration serverConfiguration = (HttpsServerConfiguration) super.serverConfiguration;

    /**
     * Create an instance for an HTTP server.
     *
     * @param httpsServerConfiguration {@code HttpsServerConfiguration} instance
     * @throws SQLException     Error while initializing database connection
     * @throws IOException      Error while reading/writing directories
     * @throws ScannerException Error while scanning for endpoints
     * @see HttpServerConfiguration
     * @see HttpServer
     * @since v1.0.0
     */
    public HttpsServer(HttpsServerConfiguration httpsServerConfiguration) throws SQLException, IOException, ScannerException {
        super(httpsServerConfiguration);
    }

    /**
     * Start HTTP server with given {@code HttpsServerConfiguration} settings. After initialization, a log wll print with the listening network
     * interface {@code address} and {@code port} (interface address {@code 0.0.0.0} simply means server listening to all network interfaces).
     *
     * @see HttpsServerConfiguration
     * @see #shutdown()
     * @see HttpServer
     * @see HttpsServer
     * @since v1.0.0
     */
    @Override
    public synchronized HttpsServer initialize() {
        if (serverThread != null && serverThread.isAlive()) throw new IllegalStateException("Server already running");

        // Simple decoration
        LogFormatter.log(logger.atInfo(), "_____ _____ __ __ _____ _____");
        LogFormatter.log(logger.atInfo(), "|   | |   __|  |  |  |  |   __|");
        LogFormatter.log(logger.atInfo(), "| | | |   __|-   -|  |  |__   |");
        LogFormatter.log(logger.atInfo(), "|_|___|_____|__|__|_____|_____| HTTP(S) (API v1.0)");

        executorService = initializeExecutorService(serverConfiguration);
        serverThread = Thread.ofPlatform().start(() -> {
            try {
                SSLServerSocketFactory sslServerSocketFactory = initializeSslContext().getServerSocketFactory();
                SSLServerSocket sslServerSocket = (SSLServerSocket) (serverConfiguration.getInetAddress() == null ?
                        sslServerSocketFactory.createServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog()) :
                        sslServerSocketFactory.createServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog(), serverConfiguration.getInetAddress()));
                sslServerSocket.setEnabledProtocols(serverConfiguration.getTlsVersions());
                serverSocket = sslServerSocket;
                serverThread.setName("Nexus-HTTP@" + serverSocket.getLocalPort());
                LogFormatter.log(logger.atInfo(), "Server initialized @ " + serverSocket.getLocalSocketAddress());
                while (!serverSocket.isClosed()) {
                    executorService.execute(new RequestConsumer(serverConfiguration, database, serverSocket.accept()));
                }
            } catch (IOException | UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }

    /**
     * Initialize SSL Context and other related Key Stores.
     *
     * @return Initialized {@code SSLContext}
     * @throws KeyStoreException         Error while loading key store
     * @throws IOException               Errors in IO
     * @throws NoSuchAlgorithmException  Impossible!
     * @throws KeyManagementException    Error while initializing key managers
     * @throws CertificateException      Certificate exception
     * @throws UnrecoverableKeyException Key exception
     * @see HttpsServerConfiguration
     * @see HttpsServer
     * @since v1.0.0
     */
    private SSLContext initializeSslContext()
            throws KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (var trustStorePath = new FileInputStream(serverConfiguration.getTrustStoreName())) {
            trustStore.load(trustStorePath, serverConfiguration.getTrustStorePassword());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (var keyStorePath = new FileInputStream(serverConfiguration.getKeyStoreName())) {
            keyStore.load(keyStorePath, serverConfiguration.getKeyStorePassword());
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, serverConfiguration.getKeyStorePassword());

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstanceStrong());
        return ctx;
    }
}
