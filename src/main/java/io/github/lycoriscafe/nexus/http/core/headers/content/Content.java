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
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Content {
    private final String contentType;
    private final long contentLength;
    private final List<TransferEncoding> transferEncodings;
    private final List<ContentEncoding> contentEncodings;
    private Object data;

    public Content(String contentType,
                   long contentLength,
                   List<TransferEncoding> transferEncodings,
                   List<ContentEncoding> contentEncodings,
                   Object data) {
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.transferEncodings = transferEncodings;
        this.contentEncodings = contentEncodings;
        this.data = data;
    }

    private void setData(final Object data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public List<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public List<ContentEncoding> getContentEncodings() {
        return contentEncodings;
    }

    public Object getData() {
        return data;
    }

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
                                      final List<TransferEncoding> transferEncoding,
                                      final List<ContentEncoding> contentEncoding,
                                      final int contentLength,
                                      final String contentType) {
        Content content = processCommonContentType(requestConsumer, transferEncoding, contentEncoding,
                contentLength, contentType);
        if (content != null) {
            content.setData(((ByteArrayOutputStream) content.getData()).toString(StandardCharsets.UTF_8));
        }
        return content;
    }

    public static Content processXWWWFormUrlencoded(final RequestConsumer requestConsumer,
                                                    final List<TransferEncoding> transferEncoding,
                                                    final List<ContentEncoding> contentEncoding,
                                                    final int contentLength) {
        if (transferEncoding != null && transferEncoding.contains(TransferEncoding.CHUNKED)) {
            requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
            return null;
        }

        Content content = processCommonContentType(requestConsumer, transferEncoding, contentEncoding,
                contentLength, "application/x-www-form-urlencoded");
        if (content != null) return null;

        Map<String, String> data = new HashMap<>();
        String[] values = ((ByteArrayOutputStream) content.getData()).toString(StandardCharsets.UTF_8)
                .split("&", 0);
        for (String value : values) {
            String[] keyVal = value.split("=", 0);
            data.put(URLDecoder.decode(keyVal[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8));
        }
        content.setData(data);

        return content;
    }

    public static Content processCommonContentType(final RequestConsumer requestConsumer,
                                                   final List<TransferEncoding> transferEncoding,
                                                   final List<ContentEncoding> contentEncoding,
                                                   final int contentLength,
                                                   final String contentType) {
        Content content;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            if (transferEncoding != null) {
                if (transferEncoding.contains(TransferEncoding.CHUNKED)) {
                    Path filePath = readTransfer(requestConsumer, transferEncoding.contains(TransferEncoding.GZIP));
                    return new Content(contentType, contentLength, transferEncoding, contentEncoding, filePath);
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

            content = new Content(contentType, contentLength, transferEncoding,
                    contentEncoding, byteArrayOutputStream);
        } catch (IOException e) {
            requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
            return null;
        }

        return content;
    }
}
