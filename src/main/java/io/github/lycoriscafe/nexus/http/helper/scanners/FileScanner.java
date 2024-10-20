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

public final class FileScanner {
    public static void scan(final HttpServerConfiguration serverConfiguration,
                            final Database database) throws ScannerException {
        if (serverConfiguration.getStaticFilesDirectory() == null) {
            return;
        }
        Path dir = Path.of(serverConfiguration.getStaticFilesDirectory());
        if (!Files.exists(dir)) {
            throw new ScannerException("static files directory is not exists");
        }
        if (!Files.isDirectory(dir)) {
            throw new ScannerException("static files directory is not a directory");
        }
        deepScan(dir, database, dir);
    }

    private static void deepScan(final Path directory,
                                 final Database database,
                                 final Path staticFilesDirectory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    deepScan(path, database, staticFilesDirectory);
                    continue;
                }

                database.addEndpointData(new ReqFile(
                        staticFilesDirectory.relativize(path).toString(),
                        Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toString(),
                        calculate(path))
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String calculate(Path path)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(path.toString()));
        byte[] buffer = new byte[1024];

        while (reader.read(buffer) > 0) {
            messageDigest.update(buffer);
        }

        reader.close();
//        return messageDigest.digest();
        // TODO implement this to return string
        return "";
    }
}
