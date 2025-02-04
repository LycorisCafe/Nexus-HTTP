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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

public final class PropertiesProcessor {
    public static HttpServerConfiguration process(boolean isHttp,
                                                  String name) throws IOException {
        HttpServerConfiguration httpServerConfiguration = isHttp ? new HttpServerConfiguration() : new HttpsServerConfiguration();
        try (var inputStream = PropertiesProcessor.class.getResourceAsStream("/" + (name == null ? "nexus-http.properties" : name))) {
            Properties properties = new Properties();
            properties.load(inputStream);

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

            // HTTPS server configuration
            if (!isHttp) return httpServerConfiguration;

            ((HttpsServerConfiguration) httpServerConfiguration).setTrustStoreName(properties.getProperty("trustStoreName"));
            ((HttpsServerConfiguration) httpServerConfiguration).setTrustStorePassword(properties.getProperty("truestStorePassword").toCharArray());
            ((HttpsServerConfiguration) httpServerConfiguration).setKeyStoreName(properties.getProperty("keyStoreName"));
            ((HttpsServerConfiguration) httpServerConfiguration).setKeyStorePassword(properties.getProperty("truestStorePassword").toCharArray());

            String[] tlsVersions = properties.getProperty("tlsVersions") == null ? null : properties.getProperty("tlsVersions").split(",", 0);
            if (tlsVersions != null) ((HttpsServerConfiguration) httpServerConfiguration).setTlsVersions(tlsVersions);

            return httpServerConfiguration;
        }
    }
}
