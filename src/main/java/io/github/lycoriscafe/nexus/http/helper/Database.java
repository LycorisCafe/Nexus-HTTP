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

import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import io.github.lycoriscafe.nexus.http.helper.models.ReqFile;
import io.github.lycoriscafe.nexus.http.helper.models.ReqMaster;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public final class Database {
    public static Connection getConnection(final String DB_LOCATION,
                                           final int PORT) throws SQLException, IOException {
        Connection conn;
        if (DB_LOCATION == null) {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        } else {
            String database = DB_LOCATION.isEmpty() ?
                    "NexusHttp" + PORT + ".db" : DB_LOCATION + "/NexusHttp" + PORT + ".db";
            File file = new File(database);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                }
            }
            conn = DriverManager.getConnection("jdbc:sqlite:" + database);
        }
        mapDatabase(conn);
        return conn;
    }

    private static void mapDatabase(final Connection conn) throws SQLException {
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

    public static void addEndpointData(Connection DATABASE,
                                       ReqMaster MODEL) throws SQLException {
        PreparedStatement masterQuery
                = DATABASE.prepareStatement("INSERT INTO ReqMaster (endpoint, reqMethod) VALUES (?, ?)");
        masterQuery.setString(1, MODEL.getEndpoint());
        masterQuery.setString(2, MODEL.getEndpoint());
        masterQuery.executeUpdate();

        Statement stmt = DATABASE.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(ROWID) FROM ReqMaster");

        switch (MODEL) {
            case ReqMaster m when m instanceof ReqEndpoint -> {
                PreparedStatement subQuery = DATABASE.prepareStatement("INSERT INTO ReqEndpoint " +
                        "(ROWID, className, methodName, statusAnnotation, statusAnnotationValue) " +
                        "VALUES (?, ?, ?, ?, ?)");
                subQuery.setInt(1, rs.getInt(1));
                subQuery.setString(2, ((ReqEndpoint) m).getClassName());
                subQuery.setString(3, ((ReqEndpoint) m).getMethodName());
                subQuery.setString(4, ((ReqEndpoint) m).getStatusAnnotation());
                subQuery.setString(5, ((ReqEndpoint) m).getStatusAnnotationValue());
                subQuery.executeUpdate();
            }
            case ReqMaster m when m instanceof ReqFile -> {
                PreparedStatement subQuery = DATABASE.prepareStatement("INSERT INTO ReqFile " +
                        "(ROWID, location, lastModified, eTag) VALUES (?, ?, ?, ?)");
                subQuery.setInt(1, rs.getInt(1));
                subQuery.setString(2, ((ReqFile) m).getLocation());
                subQuery.setString(3, ((ReqFile) m).getLastModified());
                subQuery.setString(4, ((ReqFile) m).getETag());
                subQuery.executeUpdate();
            }
            default -> throw new IllegalStateException("Unexpected value: " + MODEL);
        }
    }
}
