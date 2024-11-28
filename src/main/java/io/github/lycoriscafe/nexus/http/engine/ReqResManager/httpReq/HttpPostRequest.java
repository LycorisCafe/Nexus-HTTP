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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public sealed class HttpPostRequest extends HttpRequest permits HttpPatchRequest, HttpPutRequest {
    private Content<?> content;

    public HttpPostRequest(final RequestConsumer requestConsumer,
                           final long requestId,
                           final HttpRequestMethod requestMethod,
                           final String endpoint) {
        super(requestConsumer, requestId, requestMethod, endpoint);
    }

    public Content<?> getContent() {
        return content;
    }

    private final TransferEncoding[] transferEncoding = {TransferEncoding.NONE};
    private ContentEncoding[] contentEncoding = {ContentEncoding.NONE};
    private Integer contentLength = null;

    @Override
    public void finalizeRequest() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-type")) {
                contentEncoding = getContentEncoding();
                contentLength = getContentLength(contentEncoding != ContentEncoding.CHUNKED);

                if (contentLength == null) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return;
                }
                if (contentLength > getRequestConsumer().getServerConfiguration().getMaxContentLength()) {
                    getRequestConsumer().dropConnection(HttpStatusCode.CONTENT_TOO_LARGE);
                    return;
                }

                String value = header.getValue().toLowerCase(Locale.US);
                switch (value) {
                    case String x when x.startsWith("multipart/form-data") ->
                            processMultiPartFormData(value.split(";")[1].trim());
                    case String x when x.startsWith("text/") -> processText(value);
                    case "application/json", "application/xml" -> processText(value);
                    case "application/x-www-form-urlencoded" -> processXWWWFormUrlencoded();
                    default -> processDefault(value);
                }
                getHeaders().remove(header);
                break;
            }
        }

        super.finalizeRequest();
    }

    private void getEncodings() {
        for (Header header : getHeaders()) {
            String headerName = header.getName().toLowerCase(Locale.US);
            if (headerName.equals("transfer-encoding") || headerName.equals("content-encoding")) {
                String[] value = header.getValue().toLowerCase(Locale.US).split(",", 0);
                getHeaders().remove(header);

                switch (headerName) {
                    case "transfer-encoding" -> {

                    }
                    case "content-encoding" -> {

                    }
                }
            }
        }
    }

    private void getContentLength(final boolean mandatory) {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-length")) {
                try {
                    contentLength = Integer.parseInt(header.getValue());
                    getHeaders().remove(header);
                    return;
                } catch (NumberFormatException e) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return;
                }
            }
        }

        if (mandatory) {
            getRequestConsumer().dropConnection(HttpStatusCode.LENGTH_REQUIRED);
        }
    }

    private void processMultiPartFormData(String boundary) {
        if (contentEncoding == ContentEncoding.CHUNKED) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            suspendProcessing();
        }
    }

    private void processXWWWFormUrlencoded() {
        if (contentEncoding == ContentEncoding.CHUNKED) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            suspendProcessing();
            return;
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = switch (contentEncoding) {
                case NONE -> readContent(getRequestConsumer().getSocket().getInputStream(), contentLength);
                case GZIP -> readGzipContent(getRequestConsumer().getSocket().getInputStream(), contentLength);
                default -> throw new IllegalStateException("Unexpected value: " + contentEncoding);
            };

            if (byteArrayOutputStream.size() < contentLength) {
                getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                suspendProcessing();
                return;
            }

            String[] contentParts = byteArrayOutputStream.toString(StandardCharsets.UTF_8).split("&", 0);
            Map<String, String> parameters = new HashMap<>();
            for (String part : contentParts) {
                String[] keyVal = part.split("=");
                parameters.put(URLDecoder.decode(keyVal[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8));
            }

            content = new Content<>("application/x-www-form-urlencoded", contentLength, contentEncoding, parameters);
        } catch (IOException e) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            suspendProcessing();
        }
    }

    private void processText(String contentType) {
        // TODO process
    }

    private void processDefault(String contentType) {
        // TODO process
    }

    private static ByteArrayOutputStream readContent(final InputStream inputStream,
                                                     final int contentLength) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.writeBytes(inputStream.readNBytes(contentLength));
        return byteArrayOutputStream;
    }

    private static ByteArrayOutputStream readGzipContent(final InputStream inputStream,
                                                         final int contentLength) throws IOException {
        var gzipInputStream = new GZIPInputStream(inputStream);
        return readContent(gzipInputStream, contentLength);
    }

    private static Path readChunkedContent(final ContentEncoding contentEncoding,
                                           final RequestConsumer requestConsumer) throws IOException {
        Path filePath = Files.createTempFile(Paths.get(requestConsumer.getServerConfiguration().getTempDirectory()),
                "ChunkedContent", null);

        int chunkSize;
        InputStream inputStream;

        if (contentEncoding == ContentEncoding.GZIP) {
            inputStream = new GZIPInputStream(requestConsumer.getSocket().getInputStream());
        } else {
            inputStream = requestConsumer.getSocket().getInputStream();
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            chunkSize = Integer.parseInt(bufferedReader.readLine(), 16);
            if (chunkSize == 0) break;
            if (chunkSize > requestConsumer.getServerConfiguration().getMaxContentLength()) {
                requestConsumer.dropConnection(HttpStatusCode.BAD_REQUEST);
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
