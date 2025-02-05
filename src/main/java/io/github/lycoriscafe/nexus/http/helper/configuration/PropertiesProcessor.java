/*
 * Copyright 2025 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.helper.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Properties;

public final class PropertiesProcessor {
    /**
     * Build an instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration} by the given parameters.
     *
     * @param isHttp     If the target is {@code HttpServerConfiguration}, or else {@code HttpsServerConfiguration}
     * @param properties {@code Properties} instance of the target properties file
     * @return An instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration}
     * @throws IOException File resolving failed
     */
    public static HttpServerConfiguration process(final boolean isHttp,
                                                  final Properties properties) throws IOException {
        Objects.requireNonNull(properties);
        HttpServerConfiguration httpServerConfiguration = isHttp ? new HttpServerConfiguration() : new HttpsServerConfiguration();

        httpServerConfiguration.setBasePackage(properties.getProperty("basePackage"));
        httpServerConfiguration.setTempDirectory(properties.getProperty("tempDirectory"));

        String port = properties.getProperty("port");
        if (port != null) httpServerConfiguration.setPort(Integer.parseInt(port));

        String backlog = properties.getProperty("backlog");
        if (backlog != null) httpServerConfiguration.setBacklog(Integer.parseInt(backlog));

        String inetAddress = properties.getProperty("inetAddress");
        if (inetAddress != null) httpServerConfiguration.setInetAddress(InetAddress.getByName(inetAddress));

        String connectionTimeout = properties.getProperty("connectionTimeout");
        if (connectionTimeout != null) httpServerConfiguration.setConnectionTimeout(Integer.parseInt(connectionTimeout));

        String threadType = properties.getProperty("threadType");
        if (threadType != null) httpServerConfiguration.setThreadType(ThreadType.valueOf(threadType));

        String urlPrefix = properties.getProperty("urlPrefix");
        if (urlPrefix != null) httpServerConfiguration.setUrlPrefix(urlPrefix);

        String staticFilesDirectory = properties.getProperty("staticFilesDirectory");
        if (staticFilesDirectory != null) httpServerConfiguration.setStaticFilesDirectory(staticFilesDirectory);

        String databaseType = properties.getProperty("databaseType");
        if (databaseType != null) httpServerConfiguration.setDatabaseType(DatabaseType.valueOf(databaseType));

        String maxHeaderSize = properties.getProperty("maxHeaderSize");
        if (maxHeaderSize != null) httpServerConfiguration.setMaxHeaderSize(Integer.parseInt(maxHeaderSize));

        String maxHeadersPerRequest = properties.getProperty("maxHeadersPerRequest");
        if (maxHeadersPerRequest != null) httpServerConfiguration.setMaxHeadersPerRequest(Integer.parseInt(maxHeadersPerRequest));

        String maxIncomingConnections = properties.getProperty("maxIncomingConnections");
        if (maxIncomingConnections != null) httpServerConfiguration.setMaxIncomingConnections(Integer.parseInt(maxIncomingConnections));

        String maxContentLength = properties.getProperty("maxContentLength");
        if (maxContentLength != null) httpServerConfiguration.setMaxContentLength(Integer.parseInt(maxContentLength));

        String maxChunkedContentLength = properties.getProperty("maxChunkedContentLength");
        if (maxChunkedContentLength != null) {
            httpServerConfiguration.setMaxChunkedContentLength(Integer.parseInt(maxChunkedContentLength));
        }

        String maxChunkSize = properties.getProperty("maxChunkSize");
        if (maxChunkSize != null) httpServerConfiguration.setMaxChunkSize(Integer.parseInt(maxChunkSize));

        String addErrorMessageToResponseHeaders = properties.getProperty("addErrorMessageToResponseHeaders");
        if (addErrorMessageToResponseHeaders != null) {
            httpServerConfiguration.setAddErrorMessageToResponseHeaders(Boolean.parseBoolean(addErrorMessageToResponseHeaders));
        }

        // HTTP server configuration
        if (isHttp) return httpServerConfiguration;

        ((HttpsServerConfiguration) httpServerConfiguration).setTrustStoreName(properties.getProperty("trustStoreName"));
        ((HttpsServerConfiguration) httpServerConfiguration).setTrustStorePassword(properties.getProperty("truestStorePassword").toCharArray());
        ((HttpsServerConfiguration) httpServerConfiguration).setKeyStoreName(properties.getProperty("keyStoreName"));
        ((HttpsServerConfiguration) httpServerConfiguration).setKeyStorePassword(properties.getProperty("truestStorePassword").toCharArray());

        String[] tlsVersions = properties.getProperty("tlsVersions") == null ? null : properties.getProperty("tlsVersions").split(",", 0);
        if (tlsVersions != null) ((HttpsServerConfiguration) httpServerConfiguration).setTlsVersions(tlsVersions);

        return httpServerConfiguration;
    }

    /**
     * Build an instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration} by the given parameters.
     *
     * @param isHttp      If the target is {@code HttpServerConfiguration}, or else {@code HttpsServerConfiguration}
     * @param inputStream {@code InputStream} instance of the target properties file
     * @return An instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration}
     * @throws IOException File resolving failed
     */
    public static HttpServerConfiguration process(final boolean isHttp,
                                                  final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        Properties properties = new Properties();
        properties.load(inputStream);
        return process(isHttp, properties);
    }

    /**
     * Build an instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration} by the given parameters.
     *
     * @param isHttp   If the target is {@code HttpServerConfiguration}, or else {@code HttpsServerConfiguration}
     * @param fileName File name/location of the target properties file
     * @return An instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration}
     * @throws IOException File resolving failed
     */
    public static HttpServerConfiguration process(final boolean isHttp,
                                                  String fileName) throws IOException {
        fileName = fileName == null ? "nexus-http.properties" : fileName;

        var file = new File(fileName);
        if (file.isFile()) return process(isHttp, new FileInputStream(file));

        var inputStream = PropertiesProcessor.class.getResourceAsStream(fileName);
        if (inputStream != null) return process(isHttp, inputStream);

        return process(isHttp, PropertiesProcessor.class.getClassLoader().getResourceAsStream(fileName));
    }

    /**
     * Build an instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration} by using default properties file name.
     *
     * @param isHttp If the target is {@code HttpServerConfiguration}, or else {@code HttpsServerConfiguration}
     * @return An instance of {@code HttpServerConfiguration} or {@code HttpsServerConfiguration}
     * @throws IOException File resolving failed
     */
    public static HttpServerConfiguration process(final boolean isHttp) throws IOException {
        return process(isHttp, (String) null);
    }
}
