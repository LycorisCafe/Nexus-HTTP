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
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Content {
    private final String contentType;
    private final long contentLength = -1L;
    private String downloadName;
    private List<TransferEncoding> transferEncodings;
    private List<ContentEncoding> contentEncodings;
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
        this(contentType, (Object) data);
    }

    public Content(final String contentType,
                   final InputStream data) {
        this(contentType, (Object) data);
    }

    public String getContentType() {
        return contentType;
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

    public Content addTransferEncoding(final TransferEncoding transferEncoding) {
        Objects.requireNonNull(transferEncoding, "transfer encoding cannot be null");
        if (transferEncodings == null) transferEncodings = new NonDuplicateList<>();
        transferEncodings.add(transferEncoding);
        return this;
    }

    Content setTransferEncodings(final List<TransferEncoding> transferEncodings) {
        this.transferEncodings = Objects.requireNonNull(transferEncodings, "transfer encodings cannot be null");
        return this;
    }

    public List<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public Content addContentEncoding(final ContentEncoding contentEncoding) {
        Objects.requireNonNull(contentEncoding, "content encoding cannot be null");
        if (contentEncodings == null) contentEncodings = new NonDuplicateList<>();
        contentEncodings.add(contentEncoding);
        return this;
    }

    Content setContentEncodings(final List<ContentEncoding> contentEncodings) {
        this.contentEncodings = Objects.requireNonNull(contentEncodings, "content encodings cannot be null");
        return this;
    }

    public List<ContentEncoding> getContentEncodings() {
        return contentEncodings;
    }

    private Content setData(final Object data) {
        Objects.requireNonNull(data);
        return this;
    }

    public Object getData() {
        return data;
    }

    public static class ReadOperations {
        public static Content process(final long requestId,
                                      final RequestConsumer requestConsumer,
                                      final String contentType,
                                      final Integer contentLength,
                                      final List<TransferEncoding> transferEncodings,
                                      final List<ContentEncoding> contentEncodings) throws IOException {
            Object data = null;

            if (transferEncodings != null) {
                for (int i = transferEncodings.size() - 1; i >= 0; i--) {
                    switch (transferEncodings.get(i)) {
                        case CHUNKED -> {
                            if (data == null) {
                                data = Files.createTempFile(
                                        Paths.get(requestConsumer.getServerConfiguration().getTempDirectory()),
                                        "nexus-content-", null);
                            }
                            if (!readChunked(requestId, (Path) data, requestConsumer)) {
                                return null;
                            }
                        }
                        case GZIP -> {
                            if (data == null) {
                                if (contentLength == null) {
                                    requestConsumer.dropConnection(requestId, HttpStatusCode.LENGTH_REQUIRED,
                                            "content length required");
                                    return null;
                                }
                                data = readGzip(requestId, new byte[contentLength], requestConsumer);
                            } else {
                                data = readGzip(requestId, data, requestConsumer);
                            }
                        }
                    }
                }
            }

            if (contentEncodings != null) {
                for (int i = contentEncodings.size() - 1; i >= 0; i--) {
                    switch (contentEncodings.get(i)) {
                        case GZIP -> {
                            if (data == null) {
                                if (contentLength == null) {
                                    requestConsumer.dropConnection(requestId, HttpStatusCode.LENGTH_REQUIRED,
                                            "content length required");
                                    return null;
                                }
                                data = readGzip(requestId, new byte[contentLength], requestConsumer);
                            } else {
                                data = readGzip(requestId, data, requestConsumer);
                            }
                        }
                    }
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

            return new Content(contentType, data);
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

        private static Object readGzip(final long requestId,
                                       final Object content,
                                       final RequestConsumer requestConsumer) throws IOException {
            InputStream inputStream = requestConsumer.getSocket().getInputStream();
            GZIPInputStream gzipInputStream;
            switch (content) {
                case Path path -> {
                    gzipInputStream = new GZIPInputStream(new FileInputStream(path.toFile()));
                    Path temp = Files.createTempFile(path, "nexus-content-", null);
                    Files.write(temp, gzipInputStream.readAllBytes(), StandardOpenOption.APPEND);
                    return temp;
                }
                case byte[] bytes -> {
                    gzipInputStream = new GZIPInputStream(inputStream);
                    return gzipInputStream.read(bytes);
                }
                default -> throw new IllegalStateException("Unexpected value: " + content);
            }
        }
    }

    public static class WriteOperations {
        public static String processOutgoingContent(final HttpServerConfiguration httpServerConfiguration,
                                                    final Content content) throws IOException {
            StringBuilder result = new StringBuilder();
            result.append("Content-Type: ").append(content.getContentType()).append("\r\n");

            if (content.getContentEncodings() != null) {
                result.append("Content-Encoding: ");
                for (int i = 0; i < content.getContentEncodings().size(); i++) {
                    if (content.getContentEncodings().get(i) == ContentEncoding.GZIP) {
                        switch (content.getData()) {
                            case Path path -> {
                                Path temp = Files.createTempFile(Paths.get(httpServerConfiguration.getTempDirectory()),
                                        "nexus-content-", null);
                                GZIPOutputStream gzipOutputStream =
                                        new GZIPOutputStream(new FileOutputStream(path.toFile()));
                                try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                                    int c;
                                    while ((c = fileInputStream.read()) != -1) {
                                        gzipOutputStream.write(c);
                                    }
                                }
                                content.setData(temp);
                            }
                            case byte[] bytes -> {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                                gzipOutputStream.write(bytes);
                                content.setData(byteArrayOutputStream.toByteArray());
                            }
                            case String string -> {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                                gzipOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
                                content.setData(byteArrayOutputStream.toByteArray());
                            }
                            default -> throw new IllegalStateException("Unexpected value: " + content.getData());
                        }
                    }

                    if (i != 0) result.append(", ");
                    result.append(content.getContentEncodings().get(i).getValue());
                }
                result.append("\r\n");
            }

            if (content.getTransferEncodings() != null) {
                result.append("Transfer-Encoding: ");
                for (int i = 0; i < content.getTransferEncodings().size(); i++) {
                    if (content.getTransferEncodings().get(i) == TransferEncoding.GZIP) {
                        switch (content.getData()) {
                            case Path path -> {
                                Path temp = Files.createTempFile(Paths.get(httpServerConfiguration.getTempDirectory()),
                                        "nexus-content-", null);
                                GZIPOutputStream gzipOutputStream =
                                        new GZIPOutputStream(new FileOutputStream(path.toFile()));
                                try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                                    int c;
                                    while ((c = fileInputStream.read()) != -1) {
                                        gzipOutputStream.write(c);
                                    }
                                }
                                content.setData(temp);
                            }
                            case byte[] bytes -> {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                                gzipOutputStream.write(bytes);
                                content.setData(byteArrayOutputStream.toByteArray());
                            }
                            case String string -> {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                                gzipOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
                                content.setData(byteArrayOutputStream.toByteArray());
                            }
                            default -> throw new IllegalStateException("Unexpected value: " + content.getData());
                        }
                    }

                    if (i != 0) result.append(", ");
                    result.append(content.getTransferEncodings().get(i).getValue());
                }
                result.append("\r\n");
            }


            switch (content.getData()) {
                case Path path -> result.append("Content-Length: ").append(Files.size(path)).append("\r\n");
                case byte[] bytes -> result.append("Content-Length: ").append(bytes.length).append("\r\n");
                case String string ->
                        result.append("Content-Length: ").append(string.getBytes(StandardCharsets.UTF_8).length)
                                .append("\r\n");
                case InputStream ignored -> content.addTransferEncoding(TransferEncoding.CHUNKED);
                default -> throw new IllegalStateException("Unexpected value: " + content.getData());
            }

            if (content.getDownloadName() != null) {
                result.append("Content-Disposition: attachment; filename=\"").append(content.getDownloadName())
                        .append("\"\r\n");
            }

            return result.toString();
        }

        public static void writeContent(final RequestConsumer requestConsumer,
                                        final Content content) throws IOException {

        }
    }
}
