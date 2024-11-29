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

package io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq;

import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.headers.content.ContentEncoding;
import io.github.lycoriscafe.nexus.http.core.headers.content.TransferEncoding;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.GZIPInputStream;

public sealed class HttpPostRequest extends HttpRequest permits HttpPatchRequest, HttpPutRequest {
    private Content content;

    public HttpPostRequest(final RequestConsumer requestConsumer,
                           final long requestId,
                           final HttpRequestMethod requestMethod,
                           final String endpoint) {
        super(requestConsumer, requestId, requestMethod, endpoint);
    }

    public Content getContent() {
        return content;
    }

    private List<TransferEncoding> transferEncoding;
    private List<ContentEncoding> contentEncoding;
    private Integer contentLength = null;

    @Override
    public void finalizeRequest() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-type")) {
                if (!getEncodings()) return;
                if (getContentLength(transferEncoding == null ||
                        transferEncoding.contains(TransferEncoding.CHUNKED))) return;

                String value = header.getValue().toLowerCase(Locale.US);
                if (!switch (value) {
                    case String x when x.startsWith("multipart/form-data") ->
                            processMultiPartFormData(value.split(";")[1].trim().split("=")[1]);
                    case String x when x.startsWith("text/") -> processText(value);
                    case "application/json", "application/xml" -> processText(value);
                    case "application/x-www-form-urlencoded" -> processXWWWFormUrlencoded();
                    default -> processDefault(value);
                }) {
                    return;
                }

                getHeaders().remove(header);
                break;
            }
        }

        super.finalizeRequest();
    }

    private boolean getEncodings() {
        for (Header header : getHeaders()) {
            String headerName = header.getName().toLowerCase(Locale.US);
            if (headerName.equals("transfer-encoding") || headerName.equals("content-encoding")) {
                String[] values = header.getValue().toLowerCase(Locale.US).split(",", 0);
                getHeaders().remove(header);

                return switch (headerName) {
                    case "transfer-encoding" -> {
                        transferEncoding = new ArrayList<>();
                        for (String value : values) {
                            try {
                                transferEncoding.add(TransferEncoding.valueOf(value));
                            } catch (IllegalArgumentException e) {
                                getRequestConsumer().dropConnection(HttpStatusCode.NOT_IMPLEMENTED);
                                yield false;
                            }
                        }
                        yield true;
                    }
                    case "content-encoding" -> {
                        contentEncoding = new ArrayList<>();
                        for (String value : values) {
                            try {
                                contentEncoding.add(ContentEncoding.valueOf(value));
                            } catch (IllegalArgumentException e) {
                                getRequestConsumer().dropConnection(HttpStatusCode.NOT_IMPLEMENTED);
                                yield false;
                            }
                        }
                        yield true;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + headerName);
                };
            }
        }
        return true;
    }

    private boolean getContentLength(final boolean optional) {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-length")) {
                try {
                    contentLength = Integer.parseInt(header.getValue());

                    if (contentLength > getRequestConsumer().getServerConfiguration().getMaxContentLength()) {
                        getRequestConsumer().dropConnection(HttpStatusCode.CONTENT_TOO_LARGE);
                        return false;
                    }

                    getHeaders().remove(header);
                    return true;
                } catch (NumberFormatException e) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return false;
                }
            }
        }

        if (!optional) {
            getRequestConsumer().dropConnection(HttpStatusCode.LENGTH_REQUIRED);
            return false;
        }

        return true;
    }

    private boolean processMultiPartFormData(String boundary) {
        return true;
    }

    private boolean processXWWWFormUrlencoded() {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            if (transferEncoding != null) {
                if (transferEncoding.contains(TransferEncoding.CHUNKED)) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return false;
                } else {
                    byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(),
                            contentLength, true);
                }
            }

            if (contentEncoding != null) {
                if (contentEncoding.contains(ContentEncoding.GZIP)) {
                    if (byteArrayOutputStream == null) {
                        byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(),
                                contentLength, true);
                    } else {
                        byteArrayOutputStream = readContent(new ByteArrayInputStream(byteArrayOutputStream
                                .toByteArray()), contentLength, true);
                    }
                }
            } else {
                byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(),
                        contentLength, false);
            }

            Map<String, String> data = new HashMap<>();
            String[] values = byteArrayOutputStream.toString(StandardCharsets.UTF_8).split("&", 0);
            for (String value : values) {
                String[] keyVal = value.split("=", 0);
                data.put(URLDecoder.decode(keyVal[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8));
            }

            content = new Content("application/x-www-form-urlencoded", contentLength, transferEncoding,
                    contentEncoding, data);
        } catch (IOException e) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            return false;
        }

        return true;
    }

    private boolean processText(String contentType) {
        boolean status = processDefault(contentType);
        if (status) {
            content.setData(((ByteArrayOutputStream) content.getData()).toString(StandardCharsets.UTF_8));
        }
        return status;
    }

    private boolean processDefault(String contentType) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            if (transferEncoding != null) {
                if (transferEncoding.contains(TransferEncoding.CHUNKED)) {
                    Path filePath = readChunkedTransfer(getRequestConsumer(),
                            transferEncoding.contains(TransferEncoding.GZIP));
                    content = new Content(contentType, contentLength, transferEncoding, contentEncoding, filePath);
                } else {
                    byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(),
                            contentLength, true);
                }
            }

            if (contentEncoding != null) {
                if (contentEncoding.contains(ContentEncoding.GZIP)) {
                    if (byteArrayOutputStream == null) {
                        byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(),
                                contentLength, true);
                    } else {
                        byteArrayOutputStream = readContent(new ByteArrayInputStream(byteArrayOutputStream
                                .toByteArray()), contentLength, true);
                    }

                }
            } else {
                byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(),
                        contentLength, false);
            }

            content = new Content(contentType, contentLength, transferEncoding,
                    contentEncoding, byteArrayOutputStream);
        } catch (IOException e) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            return false;
        }

        return true;
    }

    private static ByteArrayOutputStream readContent(InputStream inputStream,
                                                     final int contentLength,
                                                     final boolean gzipped) throws IOException {
        if (gzipped) inputStream = new GZIPInputStream(inputStream);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.writeBytes(inputStream.readNBytes(contentLength));
        return byteArrayOutputStream;
    }

    private static Path readChunkedTransfer(final RequestConsumer requestConsumer,
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
}
