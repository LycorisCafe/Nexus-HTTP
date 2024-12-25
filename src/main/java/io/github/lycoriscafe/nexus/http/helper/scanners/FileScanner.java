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

package io.github.lycoriscafe.nexus.http.helper.scanners;

import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.models.ReqFile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Static files' scanner.
 *
 * @apiNote This version has the implementation, but it's not completed and will not work.
 * @see ReqFile
 * @see Database
 * @since v1.0.0
 */
public final class FileScanner {
    /**
     * Check for errors for static files directory and pass it to the scan method.
     *
     * @param serverConfiguration {@code HttpServerConfiguration} instance bound to the sever
     * @param database            {@code Database} instance bound to the server
     * @throws ScannerException Error while scanning for static files
     * @see HttpServerConfiguration
     * @see Database
     * @see FileScanner
     * @since v1.0.0
     */
    public static void scan(final HttpServerConfiguration serverConfiguration,
                            final Database database) throws ScannerException {
        if (serverConfiguration.getStaticFilesDirectory() == null) return;
        Path dir = Path.of(serverConfiguration.getStaticFilesDirectory());
        if (!Files.exists(dir)) throw new ScannerException("static files directory is not exists");
        if (!Files.isDirectory(dir)) throw new ScannerException("static files directory is not a directory");
        deepScan(dir, database, serverConfiguration);
    }

    /**
     * Scan for available static files in the provided static files directory, recursively.
     *
     * @param directory           Static files' directory
     * @param database            {@code Database} instance bound to the server
     * @param serverConfiguration {@code HttpServerConfiguration} instance bound to the server
     * @see FileScanner
     * @since v1.0.0
     */
    private static void deepScan(final Path directory,
                                 final Database database,
                                 final HttpServerConfiguration serverConfiguration) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    deepScan(path, database, serverConfiguration);
                    continue;
                }

                String endpointName = Path.of(serverConfiguration.getStaticFilesDirectory()).relativize(path).toString().replaceAll("\\\\", "/");
                database.addEndpointData(new ReqFile(endpointName, false,
                        // TODO http date format
                        Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toString(), calculateETag(path)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculate each static file E-Tag by calculating their MD5.
     *
     * @param path Path to the target static file
     * @return Calculated {@code MD5}
     * @throws NoSuchAlgorithmException Impossible!
     * @throws IOException              Error while calculating {@code MD5}
     * @see FileScanner
     * @since v1.0.0
     */
    private static String calculateETag(Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(path.toString()))) {
            byte[] buffer = new byte[1024];
            while (reader.read(buffer) > 0) {
                messageDigest.update(buffer);
            }
            return Base64.getEncoder().encodeToString(messageDigest.digest());
        }
    }
}
