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
import io.github.lycoriscafe.nexus.http.helper.models.FileReqModel;
import io.github.lycoriscafe.nexus.http.helper.models.GeneralReqModel;

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
                        ")",
                // Handle files
                "CREATE TABLE ReqFiles(" +
                        "endpoint TEXT NOT NULL," +
                        "location TEXT NOT NULL," +
                        "lastModified TEXT NOT NULL," +
                        "eTag TEXT NOT NULL" +
                        ")"
        };

        for (String query : queries) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }
    }

    public static void setEndpointDetails(final Connection DATABASE,
                                          final HttpRequestMethod REQUEST_METHOD,
                                          final GeneralReqModel REQ_MODEL) throws SQLException {
        String statement = "INSERT INTO Req" + REQUEST_METHOD.toString() + " VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = DATABASE.prepareStatement(statement);
        ps.setString(1, REQ_MODEL.getEndpoint());
        ps.setString(2, REQ_MODEL.getClassName());
        ps.setString(3, REQ_MODEL.getMethodName());
        ps.setString(4, REQ_MODEL.getStatusAnnotation());
        ps.setString(5, REQ_MODEL.getStatusAnnotationValue());
        ps.executeUpdate();
    }

    public static GeneralReqModel getEndpointDetails(final Connection DATABASE,
                                                     final HttpRequestMethod REQUEST_METHOD,
                                                     final String ENDPOINT) throws SQLException {
        String statement = "SELECT * FROM Req" + REQUEST_METHOD.toString() + " WHERE endpoint  = ?";
        PreparedStatement ps = DATABASE.prepareStatement(statement);
        ps.setString(1, ENDPOINT);
        ResultSet rs = ps.executeQuery();
        return rs.next() ?
                new GeneralReqModel(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5)) : null;
    }

    public static void setFilePathDetails(final Connection DATABASE,
                                          final FileReqModel REQ_MODEL) throws SQLException {
        String statement = "INSERT INTO Req" + REQ_MODEL.toString() + " VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = DATABASE.prepareStatement(statement);
        ps.setString(1, REQ_MODEL.getEndpoint());
        ps.setString(2, REQ_MODEL.getLocation());
        ps.setString(3, REQ_MODEL.getLastModified());
        ps.setString(4, REQ_MODEL.getETag());
        ps.executeUpdate();
    }

    public static FileReqModel getFilePathDetails(final Connection DATABASE,
                                                  final String ENDPOINT) throws SQLException {
        String statement = "SELECT * FROM ReqFiles WHERE endpoint = ?";
        PreparedStatement ps = DATABASE.prepareStatement(statement);
        ps.setString(1, ENDPOINT);
        ResultSet rs = ps.executeQuery();
        return rs.next() ?
                new FileReqModel(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4)) : null;
    }
}
