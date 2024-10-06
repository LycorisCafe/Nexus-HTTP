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

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    public static Connection getConnection(final String DB_LOCATION, final int PORT)
            throws SQLException, IOException {
        Connection conn;
        if (DB_LOCATION == null) {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        } else {
            String database = DB_LOCATION.isEmpty() ? "NexusHttp" + PORT + ".db" : DB_LOCATION + "/NexusHttp" + PORT + ".db";
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
                // Handle GET
                "CREATE TABLE ReqGET(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT" +
                        ")",
                // Handle POST
                "CREATE TABLE ReqPOST(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT" +
                        ")",
                // Handle PUT
                "CREATE TABLE ReqPUT(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT" +
                        ")",
                // Handle DELETE
                "CREATE TABLE ReqDELETE(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT" +
                        ")",
                // Handle PATCH
                "CREATE TABLE ReqPATCH(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL," +
                        "statusAnnotation TEXT," +
                        "statusAnnotationValue TEXT" +
                        ")"
        };

        for (String query : queries) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }
    }

    public static List<String> getEndpointDetails(final Connection DATABASE,
                                                  final HttpRequestMethod REQUEST_METHOD,
                                                  final String ENDPOINT) throws SQLException {
        List<String> details = new ArrayList<>();
        String statement = "SELECT * FROM Req" + REQUEST_METHOD.toString() + " WHERE endpoint  = ?";
        PreparedStatement ps = DATABASE.prepareStatement(statement);
        ps.setString(1, ENDPOINT);
        ResultSet rs = ps.executeQuery();
        for (int i = 1; i <= 5; i++) {
            details.add(rs.getString(i));
        }
        return details;
    }
}
