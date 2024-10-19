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
                        "location TEXT NOT NULL," +
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

    public synchronized void addEndpointData(final ReqMaster MODEL) throws SQLException {
        PreparedStatement masterQuery = databaseConnection
                .prepareStatement("INSERT INTO ReqMaster (endpoint, reqMethod) VALUES (?, ?)");
        masterQuery.setString(1, MODEL.getEndpoint());
        masterQuery.setString(2, MODEL.getReqMethod().name());
        masterQuery.executeUpdate();

        Statement stmt = databaseConnection.createStatement();
        int rowId = stmt.executeQuery("SELECT MAX(ROWID) FROM ReqMaster").getInt(1);

        switch (MODEL) {
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
            }
            case ReqMaster m when m instanceof ReqFile -> {
                PreparedStatement subQuery = databaseConnection.prepareStatement("INSERT INTO ReqFile " +
                        "(ROWID, location, lastModified, eTag) VALUES (?, ?, ?, ?)");
                subQuery.setInt(1, rowId);
                subQuery.setString(2, ((ReqFile) m).getLocation());
                subQuery.setString(3, ((ReqFile) m).getLastModified());
                subQuery.setString(4, ((ReqFile) m).getETag());
                subQuery.executeUpdate();
            }
            default -> throw new IllegalStateException("Unexpected value: " + MODEL);
        }
    }
}
