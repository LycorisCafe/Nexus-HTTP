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

package io.github.lycoriscafe.nexus.http.configuration;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public final class Database {
    public static Connection getConnection(final String DB_LOCATION, final int PORT)
            throws SQLException, IOException {
        Connection conn;
        if (DB_LOCATION == null) {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        } else {
            String database = DB_LOCATION.isEmpty() ? PORT + ".db" : DB_LOCATION + "/" + PORT + ".db";
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
                        "methodName TEXT NOT NULL" +
                        ")",
                // Handle POST
                "CREATE TABLE ReqPOST(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL" +
                        ")",
                // Handle PUT
                "CREATE TABLE ReqPUT(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL" +
                        ")",
                // Handle DELETE
                "CREATE TABLE ReqDELETE(" +
                        "endpoint TEXT NOT NULL," +
                        "className TEXT NOT NULL," +
                        "methodName TEXT NOT NULL" +
                        ")"
        };

        for (String query : queries) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }
    }

    public static String[] findEndpointLocation(final Connection DATABASE,
                                                final String TABLE,
                                                final String ENDPOINT) throws SQLException {
        String statement = "SELECT className, methodName FROM " + TABLE + " WHERE endpoint  = ?";
        PreparedStatement ps = DATABASE.prepareStatement(statement);
        ps.setString(1, ENDPOINT);
        ResultSet rs = ps.executeQuery();
        return rs.getString(1) == null ? null :
                new String[]{rs.getString(1), rs.getString(2)};
    }
}
