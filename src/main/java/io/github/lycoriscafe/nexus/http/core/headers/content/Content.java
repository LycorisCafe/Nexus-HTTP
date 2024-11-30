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

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class Content {
    private final String contentType;
    private long contentLength;
    private String downloadName;
    private HashSet<TransferEncoding> transferEncodings;
    private HashSet<ContentEncoding> contentEncodings;
    Object data;

    public Content(final String contentType,
                   final Path data) {
        this.contentType = contentType;
        this.data = data;
    }

    public Content(final String contentType,
                   final byte[] data) {
        this.contentType = contentType;
        this.data = data;
    }

    public Content(final String contentType,
                   final InputStream data) {
        this.contentType = contentType;
        this.data = data;
    }

    public Content setDownloadName(final String downloadName) {
        this.downloadName = downloadName;
        return this;
    }

    public Content setTransferEncodings(HashSet<TransferEncoding> transferEncodings) {
        this.transferEncodings = transferEncodings;
        return this;
    }

    public Content setContentEncodings(HashSet<ContentEncoding> contentEncodings) {
        this.contentEncodings = contentEncodings;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public HashSet<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public HashSet<ContentEncoding> getContentEncodings() {
        return contentEncodings;
    }

    public Object getData() {
        return data;
    }

    public static String processOutgoingContent(final Content content) throws IOException {
        if (content == null) {
            return "";
        }

        StringBuilder output = new StringBuilder()
                .append("Content-Type:").append(" ").append(content.getContentType()).append("\r\n");

        if (content.getDownloadName() != null) {
            output.append("Content-Disposition:").append(" ").append("attachment;").append(" ").append("filename=")
                    .append("\"").append(content.getDownloadName()).append("\"").append("\r\n");
        }

        if (content.getTransferEncodings() != null) {
            List<TransferEncoding> transferEncodingList = content.getTransferEncodings().stream().toList();
            output.append("Transfer-Encoding:");
            boolean chunked = false;
            for (int i = 0; i < transferEncodingList.size(); i++) {
                output.append(" ").append(transferEncodingList.get(i).getValue());
                if (transferEncodingList.get(i) == TransferEncoding.CHUNKED) chunked = true;
                if (i != transferEncodingList.size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");

            if (!chunked) {
                if (content.getData() instanceof Path path) {
                    output.append("Content-Length:").append(" ").append(Files.size(path)).append("\r\n");
                } else if (content.getData() instanceof byte[] array) {
                    output.append("Content-Length:").append(" ").append(array.length).append("\r\n");
                }
            }
        }

        if (content.getContentEncodings() != null) {
            List<ContentEncoding> contentEncodingList = content.getContentEncodings().stream().toList();
            output.append("Content-Encoding:");
            for (int i = 0; i < contentEncodingList.size(); i++) {
                output.append(" ").append(contentEncodingList.get(i).getValue());
                if (i != contentEncodingList.size() - 1) {
                    output.append(",");
                }
            }
            output.append("\r\n");
        }

        return output.toString();
    }

    public static class ReadOperations {
        public static ByteArrayOutputStream readContent(InputStream inputStream,
                                                        final int contentLength,
                                                        final boolean gzipped) throws IOException {
            if (gzipped) inputStream = new GZIPInputStream(inputStream);
            var byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.writeBytes(inputStream.readNBytes(contentLength));
            return byteArrayOutputStream;
        }

        private static Path readTransfer(final RequestConsumer requestConsumer,
                                         final boolean gzipped) throws IOException {
            Path filePath = Files.createTempFile(Paths.get(requestConsumer.getServerConfiguration().getTempDirectory()),
                    "ChunkedContent-", null);

            int totalSize = 0;
            int chunkSize;
            InputStream inputStream;

            if (gzipped) {
                inputStream = new GZIPInputStream(requestConsumer.getSocket().getInputStream());
            } else {
                inputStream = requestConsumer.getSocket().getInputStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                try {
                    chunkSize = Integer.parseInt(bufferedReader.readLine(), 16);
                } catch (NumberFormatException e) {
                    requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }

                if (chunkSize == 0) break;
                if (chunkSize > requestConsumer.getServerConfiguration().getMaxContentLength()) {
                    requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }

                totalSize += chunkSize;
                if (totalSize > requestConsumer.getServerConfiguration().getMaxChunkedContentLength()) {
                    requestConsumer.dropConnection(HttpStatusCode.CONTENT_TOO_LARGE);
                    return null;
                }

                byte[] buffer = new byte[chunkSize];
                int count = inputStream.read(buffer, 0, chunkSize);
                if (count != chunkSize) {
                    requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }

                if (!bufferedReader.readLine().isEmpty()) {
                    requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }

                Files.write(filePath, buffer, StandardOpenOption.APPEND);
            }
            return filePath;
        }

        public static Content processText(final RequestConsumer requestConsumer,
                                          final HashSet<TransferEncoding> transferEncoding,
                                          final HashSet<ContentEncoding> contentEncoding,
                                          final int contentLength,
                                          final String contentType) {
            Content content = processCommonContentType(requestConsumer, transferEncoding, contentEncoding,
                    contentLength, contentType);
            if (content != null) {
                content.data = (((ByteArrayOutputStream) content.getData()).toString(StandardCharsets.UTF_8));
            }
            return content;
        }

        public static Content processXWWWFormUrlencoded(final RequestConsumer requestConsumer,
                                                        final HashSet<TransferEncoding> transferEncoding,
                                                        final HashSet<ContentEncoding> contentEncoding,
                                                        final int contentLength) {
            if (transferEncoding != null && transferEncoding.contains(TransferEncoding.CHUNKED)) {
                requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                return null;
            }

            Content content = processCommonContentType(requestConsumer, transferEncoding, contentEncoding,
                    contentLength, "application/x-www-form-urlencoded");
            if (content == null) return null;

            Map<String, String> data = new HashMap<>();
            String[] values = ((ByteArrayOutputStream) content.getData()).toString(StandardCharsets.UTF_8)
                    .split("&", 0);
            for (String value : values) {
                String[] keyVal = value.split("=", 0);
                data.put(URLDecoder.decode(keyVal[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8));
            }
            content.data = data;

            return content;
        }

        public static Content processCommonContentType(final RequestConsumer requestConsumer,
                                                       final HashSet<TransferEncoding> transferEncoding,
                                                       final HashSet<ContentEncoding> contentEncoding,
                                                       final int contentLength,
                                                       final String contentType) {
            Content content;
            ByteArrayOutputStream byteArrayOutputStream = null;
            try {
                if (transferEncoding != null) {
                    if (transferEncoding.contains(TransferEncoding.CHUNKED)) {
                        Path filePath = readTransfer(requestConsumer, transferEncoding.contains(TransferEncoding.GZIP));
                        content = new Content(contentType, filePath);
                        content.contentLength = contentLength;
                        content.transferEncodings = transferEncoding;
                        content.contentEncodings = contentEncoding;
                        return content;
                    } else {
                        byteArrayOutputStream = readContent(requestConsumer.getSocket().getInputStream(),
                                contentLength, true);
                    }
                }

                if (contentEncoding != null) {
                    if (contentEncoding.contains(ContentEncoding.GZIP)) {
                        if (byteArrayOutputStream == null) {
                            byteArrayOutputStream = readContent(requestConsumer.getSocket().getInputStream(),
                                    contentLength, true);
                        } else {
                            byteArrayOutputStream = readContent(new ByteArrayInputStream(byteArrayOutputStream
                                    .toByteArray()), contentLength, true);
                        }

                    }
                } else {
                    byteArrayOutputStream = readContent(requestConsumer.getSocket().getInputStream(),
                            contentLength, false);
                }

                if (byteArrayOutputStream == null) {
                    requestConsumer.dropConnection(HttpStatusCode.INTERNAL_SERVER_ERROR);
                    return null;
                }

                content = new Content(contentType, byteArrayOutputStream.toByteArray());
                content.contentLength = contentLength;
                content.transferEncodings = transferEncoding;
                content.contentEncodings = contentEncoding;
            } catch (IOException e) {
                requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
                return null;
            }

            return content;
        }
    }
}
