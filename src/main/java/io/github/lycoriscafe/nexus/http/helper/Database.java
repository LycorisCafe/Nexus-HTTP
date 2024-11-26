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

import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest;
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
                        "reqMethod TEXT NOT NULL" +
                        ");",
                // Handle GET, POST, PUT, PATCH, DELETE
                "CREATE TABLE ReqEndpoint(" +
                        "ROWID INTEGER," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT," +
                        "FOREIGN KEY(ROWID) REFERENCES ReqMater(ROWID)" +
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
        PreparedStatement masterQuery = databaseConnection
                .prepareStatement("INSERT INTO ReqMaster (endpoint, reqMethod) VALUES (?, ?)");
        masterQuery.setString(1, model.getRequestEndpoint());
        masterQuery.setString(2, model.getReqMethod().name());
        masterQuery.executeUpdate();
        masterQuery.close();

        Statement stmt = databaseConnection.createStatement();
        int rowId = stmt.executeQuery("SELECT MAX(ROWID) FROM ReqMaster").getInt(1);
        stmt.close();

        switch (model) {
            case ReqMaster m when m instanceof ReqEndpoint -> {
                PreparedStatement subQuery = databaseConnection.prepareStatement("INSERT INTO ReqEndpoint " +
                        "(ROWID, className, methodName, statusAnnotation, statusAnnotationValue) " +
                        "VALUES (?, ?, ?, ?, ?)");
                subQuery.setInt(1, rowId);
                subQuery.setString(2, ((ReqEndpoint) m).getClassName());
                subQuery.setString(3, ((ReqEndpoint) m).getMethodName());
                subQuery.setString(4, ((ReqEndpoint) m).getStatusAnnotation());
                subQuery.setString(5, ((ReqEndpoint) m).getStatusAnnotationValue());
                subQuery.executeUpdate();
                subQuery.close();
            }
            case ReqMaster m when m instanceof ReqFile -> {
                PreparedStatement subQuery = databaseConnection.prepareStatement("INSERT INTO ReqFile " +
                        "(ROWID, lastModified, eTag) VALUES (?, ?, ?)");
                subQuery.setInt(1, rowId);
                subQuery.setString(2, ((ReqFile) m).getLastModified());
                subQuery.setString(3, ((ReqFile) m).getETag());
                subQuery.executeUpdate();
                subQuery.close();
            }
            default -> throw new IllegalStateException("Unexpected value: " + model);
        }
    }

    public ReqEndpoint getEndpointData(final HttpRequest httpRequest)
            throws SQLException, ClassNotFoundException, NoSuchMethodException {
        PreparedStatement masterQuery = databaseConnection
                .prepareStatement("SELECT COUNT(ROWID), * FROM ReqMaster WHERE endpoint = ? AND reqMethod = ?");
        masterQuery.setString(1, httpRequest.getEndpoint());
        masterQuery.setString(2, httpRequest.getRequestMethod().name());

        ResultSet rs = masterQuery.executeQuery();
        if (rs.getInt(1) == 1) {
            PreparedStatement subQuery = databaseConnection
                    .prepareStatement("SELECT * FROM ReqEndpoint WHERE ROWID = ?");
            subQuery.setInt(1, rs.getInt(2));
            ResultSet rs2 = subQuery.executeQuery();

            Class<?> clazz = Class.forName(rs2.getString(2));

            ReqEndpoint endpoint = new ReqEndpoint(
                    httpRequest.getEndpoint(),
                    httpRequest.getRequestMethod(),
                    clazz,
                    clazz.getMethod(rs2.getString(3), HttpRequest.class),
                    HttpStatusCode.valueOf(rs2.getString(4)),
                    rs2.getString(5)
            );

            rs2.close();
            subQuery.close();
            rs.close();
            masterQuery.close();

            return endpoint;
        }

        rs.close();
        masterQuery.close();
        return null;
    }
}
