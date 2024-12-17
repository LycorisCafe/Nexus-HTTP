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

package io.github.lycoriscafe.nexus.http.core.headers.content;

import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Content {
    private final String contentType;
    private long contentLength = -1L;
    private String downloadName;
    private boolean transferEncodingChunked;
    private boolean contentEncodingGzipped;
    private final Object data;

    Content(final String contentType,
            final Object data) {
        this.contentType = Objects.requireNonNull(contentType, "content type cannot be null");
        this.data = Objects.requireNonNull(data, "content data cannot be null");
    }

    public Content(final String contentType,
                   final Path data) {
        this(contentType, (Object) data);
    }

    public Content(final String contentType,
                   final byte[] data) {
        this(contentType, (Object) data);
    }

    public Content(final String contentType,
                   final String data) {
        this(contentType, (Object) (data.getBytes(StandardCharsets.UTF_8)));
    }

    public Content(final String contentType,
                   final InputStream data) {
        this(contentType, (Object) data);
        setTransferEncodingChunked(true);
    }

    public String getContentType() {
        return contentType;
    }

    private Content setContentLength(final long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public long getContentLength() {
        return contentLength;
    }

    public Content setDownloadName(final String downloadName) {
        this.downloadName = Objects.requireNonNull(downloadName, "download name cannot be null");
        return this;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public Content setTransferEncodingChunked(final boolean transferEncodingChunked) {
        this.transferEncodingChunked = transferEncodingChunked;
        return this;
    }

    public boolean isTransferEncodingChunked() {
        return transferEncodingChunked;
    }

    public Content setContentEncodingGzipped(final boolean contentEncodingGzipped) {
        this.contentEncodingGzipped = contentEncodingGzipped;
        return this;
    }

    public boolean isContentEncodingGzipped() {
        return contentEncodingGzipped;
    }

    private void setData(final Object data) {
        Objects.requireNonNull(data);
    }

    public Object getData() {
        return data;
    }

    public static class ReadOperations {
        public static Content process(final long requestId,
                                      final RequestConsumer requestConsumer,
                                      final String contentType,
                                      final Integer contentLength,
                                      final boolean chunked,
                                      final boolean gzipped) throws IOException {
            Object data = null;
            if (chunked) {
                data = Files.createTempFile(Paths.get(requestConsumer.getServerConfiguration().getTempDirectory()), "nexus-content-", null);
                if (!readChunked(requestId, (Path) data, requestConsumer)) return null;
            }

            if (gzipped) {
                if (data == null) {
                    if (contentLength == null) {
                        requestConsumer.dropConnection(requestId, HttpStatusCode.LENGTH_REQUIRED, "content length required");
                        return null;
                    }
                    data = readGzip(new byte[contentLength], requestConsumer);
                } else {
                    data = readGzip(data, requestConsumer);
                }
            }

            if (data == null) {
                data = new byte[contentLength];
                int c = requestConsumer.getSocket().getInputStream().read((byte[]) data);
                if (c != contentLength) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "connection error");
                    return null;
                }
            }

            long dataLength = switch (data) {
                case Path path -> Files.size(path);
                case byte[] bytes -> bytes.length;
                default -> -1L;
            };

            return new Content(contentType, data).setContentLength(dataLength);
        }

        private static boolean readChunked(final long requestId,
                                           final Path path,
                                           final RequestConsumer requestConsumer) throws IOException {
            InputStream inputStream = requestConsumer.getSocket().getInputStream();
            BufferedReader bufferedReader = requestConsumer.getReader();
            int totalChunkSize = 0;

            while (true) {
                int chunkSize = Integer.parseInt(bufferedReader.readLine(), 16);
                if (chunkSize == 0) break;
                totalChunkSize += chunkSize;
                if (totalChunkSize > requestConsumer.getServerConfiguration().getMaxChunkedContentLength()) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.CONTENT_TOO_LARGE,
                            "max chunked size exceeded");
                    return false;
                }

                while (true) {
                    if (chunkSize <= requestConsumer.getServerConfiguration().getMaxChunkSize()) {
                        Files.write(path, inputStream.readNBytes(chunkSize), StandardOpenOption.APPEND);
                        break;
                    } else {
                        Files.write(path,
                                inputStream.readNBytes(requestConsumer.getServerConfiguration().getMaxChunkSize()),
                                StandardOpenOption.APPEND);
                        chunkSize -= requestConsumer.getServerConfiguration().getMaxChunkSize();
                    }
                }

                long c = inputStream.skip(2);
                if (c != 2) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid chunked content");
                    return false;
                }
            }
            return true;
        }

        private static Object readGzip(final Object content,
                                       final RequestConsumer requestConsumer) throws IOException {
            switch (content) {
                case Path path -> {
                    Path temp = Files.createTempFile(Paths.get(requestConsumer.getServerConfiguration().getTempDirectory()), "nexus-content-", null);
                    try (var gzipInputStream = new GZIPInputStream(new FileInputStream(path.toFile()));
                         var fileOutputStream = new FileOutputStream(temp.toFile())) {
                        byte[] buffer = new byte[requestConsumer.getServerConfiguration().getMaxChunkSize()];
                        int c;
                        while ((c = gzipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, c);
                        }
                    }
                    return temp;
                }
                case byte[] bytes -> {
                    var gzipInputStream = new GZIPInputStream(requestConsumer.getSocket().getInputStream());
                    return gzipInputStream.read(bytes);
                }
                default -> throw new IllegalStateException("Unexpected value: " + content);
            }
        }
    }

    public static class WriteOperations {
        public static String processOutgoingContent(final HttpServerConfiguration httpServerConfiguration,
                                                    final Content content) throws IOException {
            if (content == null) return "Content-Length: 0\r\n";

            StringBuilder result = new StringBuilder();
            result.append("Content-Type: ").append(content.getContentType()).append("\r\n");

            if (content.isContentEncodingGzipped()) {
                result.append("Content-Encoding: ").append("gzip").append("\r\n");
                switch (content.getData()) {
                    case Path path -> {
                        Path temp = Files.createTempFile(Paths.get(httpServerConfiguration.getTempDirectory()), "nexus-content-", null);
                        try (var fileInputStream = new FileInputStream(path.toFile());
                             var fileOutputStream = new FileOutputStream(path.toFile());
                             var gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
                            int c;
                            byte[] buffer = new byte[httpServerConfiguration.getMaxChunkSize()];
                            while ((c = fileInputStream.read(buffer)) != -1) {
                                gzipOutputStream.write(buffer, 0, c);
                            }
                            content.setData(temp);
                        }
                    }
                    case byte[] bytes -> {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                        gzipOutputStream.write(bytes);
                        content.setData(byteArrayOutputStream.toByteArray());
                    }
                    case InputStream inputStream -> {
                        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                        content.setData(gzipInputStream);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + content.getData());
                }
            }

            if (!content.isTransferEncodingChunked()) {
                switch (content.getData()) {
                    case Path path -> result.append("Content-Length: ").append(Files.size(path)).append("\r\n");
                    case byte[] bytes -> result.append("Content-Length: ").append(bytes.length).append("\r\n");
                    case InputStream ignored -> {
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + content.getData());
                }
            } else {
                result.append("Transfer-Encoding: ").append("chunked").append("\r\n");
            }

            if (content.getDownloadName() != null) {
                result.append("Content-Disposition: attachment; filename=\"").append(content.getDownloadName())
                        .append("\"\r\n");
            }

            return result.toString();
        }

        public static void writeContent(final RequestConsumer requestConsumer,
                                        final Content content) throws IOException {
            try (InputStream inputStream = switch (content.getData()) {
                case Path path -> new FileInputStream(path.toFile());
                case byte[] bytes -> new ByteArrayInputStream(bytes);
                case InputStream stream -> stream;
                default -> throw new IllegalStateException("Unexpected value: " + content.getData());
            }) {
                int c;
                byte[] buffer = new byte[requestConsumer.getServerConfiguration().getMaxChunkSize()];
                while ((c = inputStream.read(buffer)) != -1) {
                    if (content.isTransferEncodingChunked()) {
                        requestConsumer.getSocket().getOutputStream().write((Integer.toHexString(c) + "\r\n").getBytes(StandardCharsets.UTF_8));
                    }
                    requestConsumer.getSocket().getOutputStream().write(buffer, 0, c);
                    if (content.isTransferEncodingChunked()) {
                        requestConsumer.getSocket().getOutputStream().write("\r\n".getBytes(StandardCharsets.UTF_8));
                    }
                    requestConsumer.getSocket().getOutputStream().flush();
                }
                if (content.isTransferEncodingChunked()) {
                    requestConsumer.getSocket().getOutputStream().write("0".getBytes(StandardCharsets.UTF_8));
                    requestConsumer.getSocket().getOutputStream().flush();
                }
            }
        }
    }
}
