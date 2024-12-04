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

package io.github.lycoriscafe.nexus.http.helper;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.*;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import io.github.lycoriscafe.nexus.http.helper.models.ReqFile;
import io.github.lycoriscafe.nexus.http.helper.models.ReqMaster;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public final class Database {
    private final Connection databaseConnection;

    public Database(final HttpServerConfiguration serverConfiguration) throws SQLException, IOException {
        databaseConnection = initializeDatabaseConnection(serverConfiguration);
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    public static Connection initializeDatabaseConnection(final HttpServerConfiguration serverConfiguration)
            throws SQLException, IOException {
        Connection conn;
        if (serverConfiguration.getDatabaseLocation() == null) {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        } else {
            String database = serverConfiguration.getDatabaseLocation().isEmpty() ?
                    "NexusHttp" + serverConfiguration.getPort() + ".db" :
                    serverConfiguration.getDatabaseLocation() + "/NexusHttp" + serverConfiguration.getPort() + ".db";
            File file = new File(database);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                }
            }
            conn = DriverManager.getConnection("jdbc:sqlite:" + database);
        }
        buildDatabase(conn);
        return conn;
    }

    private static void buildDatabase(final Connection conn) throws SQLException {
        String[] queries = {
                "PRAGMA foreign_keys = ON;",
                // Mater table
                "CREATE TABLE ReqMaster(" +
                        "ROWID INTEGER PRIMARY KEY," +
                        "endpoint TEXT NOT NULL," +
                        "reqMethod TEXT NOT NULL," +
                        "authenticated TEXT NOT NULL" +
                        ");",
                // Handle GET, POST, PUT, PATCH, DELETE
                "CREATE TABLE ReqEndpoint(" +
                        "ROWID INTEGER," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT," +
                        "FOREIGN KEY(ROWID) REFERENCES ReqMaster(ROWID)" +
                        ");",
                // Handle static files GET
                "CREATE TABLE ReqFile(" +
                        "ROWID INTEGER," +
                        "lastModified TEXT NOT NULL," +
                        "eTag TEXT NOT NULL," +
                        "FOREIGN KEY(ROWID) REFERENCES ReqMater(ROWID)" +
                        ");"
        };

        for (String query : queries) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }
    }

    public synchronized void addEndpointData(final ReqMaster model) throws SQLException {
        PreparedStatement masterQuery = databaseConnection.prepareStatement(
                "INSERT INTO ReqMaster (endpoint, reqMethod, authenticated) VALUES (?, ?, ?)");
        masterQuery.setString(1, model.getRequestEndpoint());
        masterQuery.setString(2, model.getReqMethod().name());
        masterQuery.setBoolean(3, model.isAuthenticated());
        masterQuery.executeUpdate();
        masterQuery.close();

        Statement stmt = databaseConnection.createStatement();
        int rowId = stmt.executeQuery("SELECT MAX(ROWID) FROM ReqMaster").getInt(1);
        stmt.close();

        switch (model) {
            case ReqEndpoint endpoint -> {
                PreparedStatement subQuery = databaseConnection.prepareStatement("INSERT INTO ReqEndpoint " +
                        "(ROWID, className, methodName, statusAnnotation, statusAnnotationValue) " +
                        "VALUES (?, ?, ?, ?, ?)");
                subQuery.setInt(1, rowId);
                subQuery.setString(2, endpoint.getClazz().getName());
                subQuery.setString(3, endpoint.getMethod().getName());
                subQuery.setString(4,
                        endpoint.getStatusAnnotation() == null ? null : endpoint.getStatusAnnotation().toString());
                subQuery.setString(5, endpoint.getStatusAnnotationValue());
                subQuery.executeUpdate();
                subQuery.close();
            }
            case ReqFile file -> {
                PreparedStatement subQuery = databaseConnection.prepareStatement(
                        "INSERT INTO ReqFile " + "(ROWID, lastModified, eTag) VALUES (?, ?, ?)");
                subQuery.setInt(1, rowId);
                subQuery.setString(2, file.getLastModified());
                subQuery.setString(3, file.getETag());
                subQuery.executeUpdate();
                subQuery.close();
            }
            default -> throw new IllegalStateException("Unexpected value: " + model);
        }
    }

    public ReqEndpoint getEndpointData(final HttpRequest httpRequest)
            throws SQLException, ClassNotFoundException, NoSuchMethodException {
        PreparedStatement masterQuery = databaseConnection.prepareStatement(
                "SELECT COUNT(ROWID), * FROM ReqMaster WHERE endpoint = ? AND reqMethod = ?");
        masterQuery.setString(1, httpRequest.getEndpoint());
        masterQuery.setString(2, httpRequest.getRequestMethod().name());

        ResultSet rs = masterQuery.executeQuery();
        if (rs.getInt(1) == 1) {
            PreparedStatement subQuery =
                    databaseConnection.prepareStatement("SELECT * FROM ReqEndpoint WHERE ROWID = ?");
            subQuery.setInt(1, rs.getInt(2));
            ResultSet rs2 = subQuery.executeQuery();
            ReqEndpoint endpoint;

            try {
                Class<?> clazz = Class.forName(rs2.getString(2));
                endpoint = new ReqEndpoint(rs.getString(3),
                        HttpRequestMethod.valueOf(rs.getString(4)),
                        rs.getBoolean(5), clazz,
                        clazz.getMethod(rs2.getString(3), switch (httpRequest.getRequestMethod()) {
                            case CONNECT, TRACE -> null;
                            case DELETE -> HttpDeleteRequest.class;
                            case GET -> HttpGetRequest.class;
                            case HEAD -> HttpHeadRequest.class;
                            case OPTIONS -> HttpOptionsRequest.class;
                            case PATCH -> HttpPatchRequest.class;
                            case POST -> HttpPostRequest.class;
                            case PUT -> HttpPutRequest.class;
                        }), rs2.getString(4) == null ? null : HttpStatusCode.valueOf(rs2.getString(4)),
                        rs2.getString(5));
            } finally {
                rs2.close();
                subQuery.close();
                rs.close();
                masterQuery.close();
            }

            return endpoint;
        }

        rs.close();
        masterQuery.close();
        return null;
    }
}
