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
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class Content {
    private final String contentType;
    private long contentLength;
    private String downloadName;
    private List<TransferEncoding> transferEncodings;
    private List<ContentEncoding> contentEncodings;
    private Object data;

    Content(final String contentType) {
        this.contentType = contentType;
    }

    private Content(final String contentType,
                    final Object data) {
        this.contentType = contentType;
        this.data = data;
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
        this.downloadName = downloadName;
        return this;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public Content addTransferEncoding(final TransferEncoding transferEncoding) {
        if (transferEncodings == null) transferEncodings = new NonDuplicateList<>();
        transferEncodings.add(transferEncoding);
        return this;
    }

    public List<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public Content addContentEncoding(final ContentEncoding contentEncoding) {
        if (contentEncodings == null) contentEncodings = new NonDuplicateList<>();
        contentEncodings.add(contentEncoding);
        return this;
    }

    public List<ContentEncoding> getContentEncodings() {
        return contentEncodings;
    }

    Content setData(final Object data) {
        this.data = data;
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
            Object content = null;

            for (int i = transferEncodings.size() - 1; i >= 0; i--) {
                switch (transferEncodings.get(i)) {
                    case CHUNKED -> {
                        if (content == null) {
                            content = Files.createTempFile(
                                    Paths.get(requestConsumer.getServerConfiguration().getTempDirectory()),
                                    "nexus-content-", null);
                        }
                        if (!readChunked(requestId, (Path) content, requestConsumer)) {
                            return null;
                        }
                    }
                    case GZIP -> {
                        if (content == null) {
                            if (contentLength == null) {
                                requestConsumer.dropConnection(requestId, HttpStatusCode.LENGTH_REQUIRED,
                                        "content length required");
                                return null;
                            }
                            content = readGzip(requestId, new byte[contentLength], requestConsumer);
                        } else {
                            content = readGzip(requestId, content, requestConsumer);
                        }
                    }
                }
            }

            for (ContentEncoding contentEncoding : contentEncodings) {
                switch (contentEncoding) {
                    case GZIP -> {

                    }
                }
            }
            return null;
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

    }
}
