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

package io.github.lycoriscafe.nexus.http.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.lycoriscafe.nexus.http.core.headers.auth.AuthScheme;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerTokenRequest;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq.*;
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.configuration.DatabaseType;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.configuration.ThreadType;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import io.github.lycoriscafe.nexus.http.helper.models.ReqFile;
import io.github.lycoriscafe.nexus.http.helper.models.ReqMaster;
import io.github.lycoriscafe.nexus.http.helper.scanners.ScannerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Database for store endpoint details.
 *
 * @see ReqMaster
 * @since v1.0.0
 */
public final class Database {
    private final HikariDataSource dataSource;

    /**
     * Create instance of {@code Database} and initialize connection according to {@code HttpServerConfiguration} settings.
     *
     * @param serverConfiguration {@code HttpServerConfiguration} bound to the server
     * @throws SQLException Error while establishing the database connection
     * @throws IOException  Error while writing the database to disk
     * @see Database
     * @since v1.0.0
     */
    public Database(final HttpServerConfiguration serverConfiguration) throws SQLException, IOException {
        Objects.requireNonNull(serverConfiguration);
        dataSource = initializeDatabaseConnection(serverConfiguration);
        buildDatabase(dataSource.getConnection());
    }

    /**
     * Get initialized database connection.
     *
     * @return Database connection.
     * @see Database
     * @since v1.0.0
     */
    public Connection getDatabaseConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Initialize database pool using HikariCP.
     *
     * @param serverConfiguration {@code HttpServerConfiguration} bound to the server
     * @return Established connection pool
     * @see Database
     * @since v1.0.0
     */
    public static HikariDataSource initializeDatabaseConnection(final HttpServerConfiguration serverConfiguration) throws IOException {
        if (serverConfiguration.getDatabaseType() == DatabaseType.TEMPORARY) {
            Path path = Paths.get(serverConfiguration.getTempDirectory() + "/NexusHttp.db");
            if (Files.exists(path)) Files.delete(path);
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        hikariConfig.setJdbcUrl(serverConfiguration.getDatabaseType() == DatabaseType.TEMPORARY ?
                "jdbc:sqlite:" + serverConfiguration.getTempDirectory() + "/NexusHttp.db" : "jdbc:sqlite::memory:");
        hikariConfig.setPoolName("Nexus-HTTP Connection Pool");
        hikariConfig.setConnectionInitSql("PRAGMA foreign_keys = ON");
        hikariConfig.setLeakDetectionThreshold(10_000L);
        hikariConfig.setThreadFactory(serverConfiguration.getThreadType() == ThreadType.PLATFORM ?
                Thread.ofPlatform().factory() : Thread.ofVirtual().factory());
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Construct database structure.
     *
     * @param conn Established database connection
     * @throws SQLException Error while writing to the database
     * @see Database
     * @since v1.0.0
     */
    private static void buildDatabase(final Connection conn) throws SQLException {
        Objects.requireNonNull(conn);
        String[] queries = {
                // Master table
                """
                CREATE TABLE ReqMaster(
                    ROWID INTEGER PRIMARY KEY,
                    endpoint TEXT NOT NULL,
                    reqMethod TEXT NOT NULL,
                    authenticated TEXT NOT NULL,
                    type TEXT NOT NULL
                );""",
                // Handle GET, POST, PUT, PATCH, DELETE
                """
                CREATE TABLE ReqEndpoint(" +
                    ROWID INTEGER,
                    className TEXT NOT NULL,
                    methodName TEXT NOT NULL,
                    authSchemeAnnotation TEXT,
                    FOREIGN KEY(ROWID) REFERENCES ReqMaster(ROWID)
                );""",
                // Handle static files GET
                """
                CREATE TABLE ReqFile(
                    ROWID INTEGER,
                    lastModified TEXT NOT NULL,
                    eTag TEXT NOT NULL,
                    FOREIGN KEY(ROWID) REFERENCES ReqMater(ROWID)
                );"""
        };

        for (String query : queries) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }
    }

    /**
     * Add endpoint data to the database.
     *
     * @param model {@code ReqMaster} or its child instance
     * @throws SQLException     Error while writing data to the database
     * @throws ScannerException More than one endpoint with the same URi found error
     * @see ReqMaster
     * @see Database
     * @since v1.0.0
     */
    public synchronized void addEndpointData(final ReqMaster model) throws SQLException, ScannerException {
        try (Connection databaseConnection = getDatabaseConnection()) {
            try (PreparedStatement preQuery = databaseConnection.prepareStatement("SELECT COUNT(endpoint) FROM ReqMaster " +
                    "WHERE endpoint = ? AND reqMethod = ? COLLATE NOCASE")) {
                preQuery.setString(1, model.getRequestEndpoint());
                preQuery.setString(2, model.getReqMethod().toString());
                try (ResultSet preResultSet = preQuery.executeQuery()) {
                    if (preResultSet.getInt(1) != 0) {
                        throw new ScannerException("endpoints with same value found, aborting scanning");
                    }
                }
            }

            int rowId;
            try (PreparedStatement masterQuery = databaseConnection.prepareStatement("INSERT INTO ReqMaster " +
                    "(endpoint, reqMethod, authenticated, type) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                masterQuery.setString(1, model.getRequestEndpoint());
                masterQuery.setString(2, model.getReqMethod().toString());
                masterQuery.setBoolean(3, model.isAuthenticated());
                masterQuery.setString(4, model instanceof ReqEndpoint ? "endpoint" : "file");
                if (masterQuery.executeUpdate() != 1) {
                    throw new ScannerException("Error while inserting data to the database");
                }
                try (ResultSet masterResultSet = masterQuery.getGeneratedKeys()) {
                    rowId = masterResultSet.getInt(1);
                }
            }

            switch (model) {
                case ReqEndpoint endpoint -> {
                    try (PreparedStatement subQuery = databaseConnection.prepareStatement("INSERT INTO ReqEndpoint " +
                            "(ROWID, className, methodName, authSchemeAnnotation) VALUES (?, ?, ?, ?)")) {
                        subQuery.setInt(1, rowId);
                        subQuery.setString(2, endpoint.getClazz().getName());
                        subQuery.setString(3, endpoint.getMethod().getName());
                        subQuery.setString(4, endpoint.getAuthSchemeAnnotation() == null ?
                                null : endpoint.getAuthSchemeAnnotation().toString());
                        if (subQuery.executeUpdate() != 1) {
                            throw new ScannerException("Error while inserting data to the database");
                        }
                    }
                }
                case ReqFile file -> {
                    try (PreparedStatement subQuery = databaseConnection.prepareStatement("INSERT INTO ReqFile " +
                            "(ROWID, lastModified, eTag) VALUES (?, ?, ?)")) {
                        subQuery.setInt(1, rowId);
                        subQuery.setString(2, file.getLastModified());
                        subQuery.setString(3, file.getETag());
                        if (subQuery.executeUpdate() != 1) {
                            throw new ScannerException("Error while inserting data to the database");
                        }
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + model);
            }
        }
    }

    /**
     * Get all {@code ReqMaster} instances for requested URI.
     *
     * @param httpRequest {@code HttpRequest} instance
     * @return All possible {@code ReqMaster} models
     * @throws SQLException           Error while reading from the database
     * @throws ClassNotFoundException Error while casting the class
     * @throws NoSuchMethodException  Error while casting the method
     * @see HttpRequest
     * @see ReqMaster
     * @see Database
     * @since v1.0.0
     */
    public List<ReqMaster> getEndpointData(final HttpRequest httpRequest) throws SQLException, ClassNotFoundException, NoSuchMethodException {
        List<ReqMaster> endpoints = new ArrayList<>();
        try (Connection databaseConnection = getDatabaseConnection();
             PreparedStatement masterQuery = databaseConnection.prepareStatement("SELECT * FROM ReqMaster WHERE endpoint = ? COLLATE NOCASE")) {
            masterQuery.setString(1, httpRequest.getEndpoint());
            try (ResultSet masterResult = masterQuery.executeQuery()) {
                while (masterResult.next()) {
                    switch (masterResult.getString(5)) {
                        case "endpoint" -> {
                            ReqEndpoint endpoint;
                            try (PreparedStatement subQuery = databaseConnection.prepareStatement("SELECT * FROM ReqEndpoint WHERE ROWID = ?")) {
                                subQuery.setInt(1, masterResult.getInt(1));
                                try (ResultSet subResult = subQuery.executeQuery()) {
                                    Class<?> clazz = Class.forName(subResult.getString(2));
                                    AuthScheme authScheme = (subResult.getString(4) == null ?
                                            null : AuthScheme.valueOf(subResult.getString(4)));
                                    Class<?> requestParamType = null;
                                    Class<?> responseParamType = null;
                                    if (authScheme == null) {
                                        requestParamType = switch (HttpRequestMethod.valueOf(masterResult.getString(3))) {
                                            case DELETE -> HttpDeleteRequest.class;
                                            case GET -> HttpGetRequest.class;
                                            case HEAD -> HttpHeadRequest.class;
                                            case OPTIONS -> HttpOptionsRequest.class;
                                            case PATCH -> HttpPatchRequest.class;
                                            case POST -> HttpPostRequest.class;
                                            case PUT -> HttpPutRequest.class;
                                        };
                                        responseParamType = HttpResponse.class;
                                    } else {
                                        switch (authScheme) {
                                            case BASIC -> {}
                                            case BEARER -> requestParamType = BearerTokenRequest.class;
                                        }
                                    }

                                    endpoint = new ReqEndpoint(masterResult.getString(2), HttpRequestMethod.valueOf(masterResult.getString(3)),
                                            masterResult.getBoolean(4), clazz,
                                            responseParamType == null ? clazz.getMethod(subResult.getString(3), requestParamType) :
                                                    clazz.getMethod(subResult.getString(3), requestParamType, responseParamType), authScheme);
                                }
                            }
                            endpoints.add(endpoint);
                        }
                        case "file" -> {
                            // TODO implement for file access
                        }
                    }
                }
            }
        }
        return endpoints;
    }
}
