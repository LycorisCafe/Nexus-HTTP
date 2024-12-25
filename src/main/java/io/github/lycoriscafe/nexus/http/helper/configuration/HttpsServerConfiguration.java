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

package io.github.lycoriscafe.nexus.http.helper.configuration;

import io.github.lycoriscafe.nexus.http.HttpsServer;

import java.util.Objects;

/**
 * Configurations for {@code HttpsServer}.
 *
 * @see HttpsServer
 * @see HttpServerConfiguration
 * @since v1.0.0
 */
public final class HttpsServerConfiguration extends HttpServerConfiguration {
    private final String trustStoreName;
    private final char[] trustStorePassword;
    private final String keyStoreName;
    private final char[] keyStorePassword;
    private final String[] tlsVersions;

    /**
     * Create an instance of {@code HttpServerConfiguration}.
     *
     * @param basePackage   Package that needs to scan for {@code HttpEndpoint} classes. Sub packages will be also included.
     * @param tempDirectory Temporary directory location for in-API server tasks. It must be separated directories if you implement more than one
     *                      server. If you didn't specify the {@code tlsVersions}, {@code TLSv1.3} will be used.
     * @apiNote <pre>
     * {@code
     * // Example endpoint scan scenario
     * var serverConfig = new HttpServerConfiguration("org.example.server_x", "TempServer_x",
     *      "path/trustStore", "password".toCharArray(), "path/keyStore", "password".toCharArray());
     * _
     * |- org.example
     *      |- server_x
     *          |- Class_1.java [included]
     *          |- Class_2.java [included]
     *          |- sub_package_1
     *              |- Class_3.java [included]
     *      |- server_y
     *          |- Class_4.java [not included]
     *          |- Class_5.java [not included]
     *          |- sub_package_2
     *              |- Class_6.java [not included]
     * }
     * </pre>
     * @see HttpsServerConfiguration
     * @since v1.0.0
     */
    public HttpsServerConfiguration(final String basePackage,
                                    final String tempDirectory,
                                    final String trustStoreName,
                                    final char[] trustStorePassword,
                                    final String keyStoreName,
                                    final char[] keyStorePassword,
                                    final String... tlsVersions) {
        super(basePackage, tempDirectory);
        this.trustStoreName = Objects.requireNonNull(trustStoreName);
        this.trustStorePassword = trustStorePassword;
        this.keyStoreName = Objects.requireNonNull(keyStoreName);
        this.keyStorePassword = keyStorePassword;
        this.tlsVersions = tlsVersions.length == 0 ? new String[]{"TLSv1.3"} : tlsVersions;
        setPort(443);
    }

    /**
     * Get provided trust store path.
     *
     * @return Trust store path
     * @see HttpsServerConfiguration
     * @since v1.0.0
     */
    public String getTrustStoreName() {
        return trustStoreName;
    }

    /**
     * Get provided trust store password.
     *
     * @return Trust store password
     * @see HttpsServerConfiguration
     * @since v1.0.0
     */
    public char[] getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Get provided key store.
     *
     * @return Key store
     * @see HttpsServerConfiguration
     * @since v1.0.0
     */
    public String getKeyStoreName() {
        return keyStoreName;
    }

    /**
     * Get provided key store password.
     *
     * @return Key store password
     * @see HttpsServerConfiguration
     * @since v1.0.0
     */
    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    /**
     * Get provided {@code TLS} versions.
     *
     * @return {@code TLS} versions
     * @see HttpsServerConfiguration
     * @since v1.0.0
     */
    public String[] getTlsVersions() {
        return tlsVersions;
    }
}
