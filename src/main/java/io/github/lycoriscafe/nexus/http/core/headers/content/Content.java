/*
 * Copyright 2025 Lycoris Café
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * HTTP request/response content.
 * <pre>
 *     {@code
 *     // Example code
 *     var content = new Content("video/mp4", Paths.get("path/to/mp4/video"))
 *          .setTransferEncodingChunked(true)
 *          .setDownloadName("SampleVideo.mp4");
 *     }
 * </pre>
 *
 * @apiNote This version of API only supports {@code Transfer-Encoding} <b>chunked</b> and {@code Content-Encoding} <b>gzip</b> for incoming and
 * outgoing content related encodings. {@code Content negotiation}, {@code Conditional requests} and {@code Range requests} are not yet supported by
 * the server itself, but the API users can implement it appropriately in their code.
 * @see #Content(String, Path)
 * @see #Content(String, byte[])
 * @see #Content(String, String)
 * @see #Content(String, InputStream)
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9110#name-representation-data-and-met">HTTP Semantics (rfc9110) - 8. Representation Data and
 * Metadata</a>
 * @since v1.0.0
 */
public final class Content {
    private static final Logger logger = LoggerFactory.getLogger(Content.class);

    private final String contentType;
    private String downloadName;
    private boolean transferEncodingChunked;
    private boolean contentEncodingGzipped;
    private final Object data;

    /**
     * Parent constructor for instancing {@code Content}.
     *
     * @param contentType {@code Content-Type} of the provided data
     * @param data        Data that received/going to send
     * @apiNote Only used for in-API tasks.
     * @see Content
     * @since v1.0.0
     */
    Content(final String contentType,
            final Object data) {
        this.contentType = Objects.requireNonNull(contentType, "content type cannot be null");
        this.data = Objects.requireNonNull(data, "content data cannot be null");
    }

    /**
     * Create an instance of {@code Content} by providing {@code Path} as data.
     * <pre>
     *     {@code
     *     // Example code
     *     Path someVideoPath = Paths.get("path/to/a/video");
     *     var content = new Content("video/mp4", someVideoPath);
     *     }
     * </pre>
     *
     * @param contentType {@code Content-Type} of the provided data
     * @param data        {@code Path} of data
     * @see Path
     * @see Content
     * @since v1.0.0
     */
    public Content(final String contentType,
                   final Path data) {
        this(contentType, (Object) data);
    }

    /**
     * Create an instance of {@code Content} by providing {@code byte[]} as data.
     * <pre>
     *     {@code
     *     // Example code
     *     byte[] someByteArray = ...
     *     var content = new Content("application/octet-stream", someByteArray);
     *     }
     * </pre>
     *
     * @param contentType {@code Content-Type} of the provided data
     * @param data        {@code byte[]} of data
     * @see Content
     * @since v1.0.0
     */
    public Content(final String contentType,
                   final byte[] data) {
        this(contentType, (Object) data);
    }

    /**
     * Create an instance of {@code Content} by providing {@code String} as data.
     * <pre>
     *     {@code
     *     // Example code
     *     String someString = "this is a sample string";
     *     var content = new Content("text/plain", someString);
     *     }
     * </pre>
     *
     * @param contentType {@code Content-Type} of the provided data
     * @param data        {@code String} of data
     * @see Content
     * @since v1.0.0
     */
    public Content(final String contentType,
                   final String data) {
        this(contentType, (Object) (data.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Create an instance of {@code Content} by providing {@code InputStream} as data.
     * <pre>
     *     {@code
     *     // Example code
     *     var inputStream = new FileInputStream("path/to/a/pdf/file");
     *     var content = new Content("application/pdf", inputStream);
     *     }
     * </pre>
     *
     * @param contentType {@code Content-Type} of the provided data
     * @param data        {@code InputStream} of data
     * @see Content
     * @since v1.0.0
     */
    public Content(final String contentType,
                   final InputStream data) {
        this(contentType, (Object) data);
        setTransferEncodingChunked(true);
    }

    /**
     * Get the provided content type.
     *
     * @return Provided content type
     * @see Content
     * @since v1.0.0
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the name for content downloading. If this is set, the {@code Content-Disposition: attachment; filename="fileName"} header will present in the
     * request and browsers will pop a download window.
     * <pre>
     *     {@code
     *     // Example code
     *     var path = Paths.get("SampleFile.exe");
     *     var content = new Content("application/octet-stream", path)
     *          .setDownloadName("SampleFile.exe");
     *     }
     * </pre>
     *
     * @param downloadName Content name for the downloading
     * @return Same {@code Content} instance
     * @see Content
     * @since v1.0.0
     */
    public Content setDownloadName(final String downloadName) {
        this.downloadName = Objects.requireNonNull(downloadName);
        return this;
    }

    /**
     * Get provided download name.
     *
     * @return Download name
     * @see #setDownloadName(String)
     * @see Content
     * @since v1.0.0
     */
    public String getDownloadName() {
        return downloadName;
    }

    /**
     * Set {@code Transfer-Encoding} to <b>'chunked'</b>. When this is enabled, default chunk size will get by the {@code HttpServerConfiguration}.
     *
     * @param transferEncodingChunked Set/Unset {@code Transfer-Encoding} to <b>'chunked'</b>
     * @return Same {@code Content} instance
     * @see HttpServerConfiguration#setMaxChunkSize(int)
     * @see Content
     * @since v1.0.0
     */
    public Content setTransferEncodingChunked(final boolean transferEncodingChunked) {
        this.transferEncodingChunked = transferEncodingChunked;
        return this;
    }

    /**
     * Get is {@code Transfer-Encoding} set to <b>'chunked'</b>.
     *
     * @return {@code Transfer-Encoding} <b>chunked</b> status
     * @see #setTransferEncodingChunked(boolean)
     * @see Content
     * @since v1.0.0
     */
    public boolean isTransferEncodingChunked() {
        return transferEncodingChunked;
    }

    /**
     * Set {@code Content-Encoding} to <b>gzip</b>.
     *
     * @param contentEncodingGzipped Set/Unset {@code Content-Encoding} to <b>gzip</b>
     * @return Same {@code Content} instance
     * @see Content
     * @since v1.0.0
     */
    public Content setContentEncodingGzipped(final boolean contentEncodingGzipped) {
        if (data instanceof InputStream) throw new IllegalStateException("input stream with gzip not yet supported");
        this.contentEncodingGzipped = contentEncodingGzipped;
        return this;
    }

    /**
     * Get is {@code Content-Encoding} set to <b>gzip</b>.
     *
     * @return {@code Content-Encoding} <b>gzip</b> status
     * @see #setContentEncodingGzipped(boolean)
     * @see Content
     * @since v1.0.0
     */
    public boolean isContentEncodingGzipped() {
        return contentEncodingGzipped;
    }

    /**
     * Set content data
     *
     * @param data Content data
     * @apiNote This method is only for the in-API tasks.
     * @see Content
     * @since v1.0.0
     */
    private void setData(final Object data) {
        Objects.requireNonNull(data);
    }

    /**
     * Get content data. This method always returns an {@code Object}. API users need to implement their own way to handle data by using the
     * {@code Content-Type}.
     *
     * @return Data as {@code Object}
     * @apiNote When receiving,
     * <ul>
     *  <li>{@code multipart/form-data}, the data should be cast to {@code List} of {@code MultipartFormData>}.</li>
     *  <li>{@code application/x-www-form-urlencoded}, the data should cast to {@code UrlEncodedData}.</li>
     *  <li>{@code Transfer-Encoding: chunked}, the data should cast to {@code Path}.</li>
     *  <li>Others should cast to {@code byte[]}.</li>
     * </ul>
     * @see #getContentType()
     * @see MultipartFormData
     * @see UrlEncodedData
     * @see Path
     * @see Content
     * @since v1.0.0
     */
    public Object getData() {
        return data;
    }

    /**
     * Used for read content data from socket connection.
     *
     * @apiNote This class is public but not useful for the API users. Only used for in-API tasks.
     * @see Content
     * @since v1.0.0
     */
    public static class ReadOperations {
        /**
         * General processor for incoming content.
         *
         * @param requestId       {@code HttpRequest} id
         * @param requestConsumer {@code RequestConsumer} bound to the {@code HttpRequest}
         * @param contentType     {@code Content-Type}
         * @param contentLength   {@code Content-Length}
         * @param chunked         {@code Transfer-Encoding} chunked?
         * @param gzipped         {@code Content-Encoding} gzipped?
         * @return New instance of {@code Content}
         * @throws IOException Error while reading data from the socket input stream
         * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
         * @see RequestConsumer
         * @see Content
         * @since v1.0.0
         */
        public static Content process(final long requestId,
                                      final RequestConsumer requestConsumer,
                                      final String contentType,
                                      final Integer contentLength,
                                      final boolean chunked,
                                      final boolean gzipped) throws IOException {
            Object data = null;
            if (chunked) {
                data = Files.createTempFile(Paths.get(requestConsumer.getHttpServerConfiguration().getTempDirectory()), "nexus-content-", null);
                if (!readChunked(requestId, (Path) data, requestConsumer)) return null;
            }

            if (gzipped) data = readGzip(Objects.requireNonNullElseGet(data, () -> new byte[contentLength]), requestConsumer);

            if (data == null) {
                byte[] buffer = new byte[contentLength];
                int c = requestConsumer.getSocket().getInputStream().readNBytes(buffer, 0, contentLength);
                if (c != contentLength) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "connection error", logger);
                    return null;
                }
                data = buffer;
            }

            return new Content(contentType, data);
        }

        /**
         * Read chunked content from the socket input stream.
         *
         * @param requestId       {@code HttpRequest} id
         * @param path            {@code Path} that going to store the chunked content
         * @param requestConsumer {@code RequestConsumer} bound to the {@code HttpRequest}
         * @return Read process success or fail status
         * @throws IOException Error while reading data from the socket input stream
         * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
         * @see RequestConsumer
         * @see Path
         * @since v1.0.0
         */
        private static boolean readChunked(final long requestId,
                                           final Path path,
                                           final RequestConsumer requestConsumer) throws IOException {
            InputStream inputStream = requestConsumer.getSocket().getInputStream();
            int totalChunkSize = 0;

            while (true) {
                String line = requestConsumer.readLine();
                if (line == null) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "content cannot process", logger);
                    return false;
                }

                int chunkSize = Integer.parseInt(line, 16);
                if (chunkSize == 0) break;
                totalChunkSize += chunkSize;
                if (totalChunkSize > requestConsumer.getHttpServerConfiguration().getMaxChunkedContentLength()) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.CONTENT_TOO_LARGE, "max chunked size exceeded", logger);
                    return false;
                }

                while (true) {
                    if (chunkSize <= requestConsumer.getHttpServerConfiguration().getMaxChunkSize()) {
                        Files.write(path, inputStream.readNBytes(chunkSize), StandardOpenOption.APPEND);
                        break;
                    } else {
                        Files.write(path, inputStream.readNBytes(requestConsumer.getHttpServerConfiguration()
                                .getMaxChunkSize()), StandardOpenOption.APPEND);
                        chunkSize -= requestConsumer.getHttpServerConfiguration().getMaxChunkSize();
                    }
                }

                long c = inputStream.skip(2);
                if (c != 2) {
                    requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "invalid chunked content", logger);
                    return false;
                }
            }
            return true;
        }

        /**
         * Decompress gzip content.
         *
         * @param content         Received content
         * @param requestConsumer {@code RequestConsumer} bound to the {@code HttpRequest}
         * @return Type of decompressed data
         * @throws IOException Error while decompressing data
         * @see RequestConsumer
         * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
         * @since v1.0.0
         */
        private static Object readGzip(final Object content,
                                       final RequestConsumer requestConsumer) throws IOException {
            switch (content) {
                case Path path -> {
                    Path temp = Files.createTempFile(Paths.get(requestConsumer.getHttpServerConfiguration()
                            .getTempDirectory()), "nexus-content-", null);
                    try (var gzipInputStream = new GZIPInputStream(new FileInputStream(path.toFile()), requestConsumer.getHttpServerConfiguration()
                            .getMaxChunkSize());
                         var fileOutputStream = new FileOutputStream(temp.toFile())) {
                        int c;
                        byte[] buffer = new byte[requestConsumer.getHttpServerConfiguration().getMaxChunkSize()];
                        while ((c = gzipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, c);
                        }
                    }
                    return temp;
                }
                case byte[] bytes -> {
                    try (var gzipInputStream = new GZIPInputStream(requestConsumer.getSocket()
                            .getInputStream(), requestConsumer.getHttpServerConfiguration().getMaxChunkSize())) {
                        return gzipInputStream.read(bytes);
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + content);
            }
        }
    }

    /**
     * Used to write content data to socket connection.
     *
     * @apiNote This class is public but not useful for the API users. Only used for in-API tasks.
     * @see Content
     * @since v1.0.0
     */
    public static class WriteOperations {
        // TODO content encoding (gzip) has bugs

        /**
         * Process headers and data (like gzip) to send along with the {@code HttpResponse}.
         *
         * @param httpServerConfiguration {@code HttpServerConfiguration}
         * @param content                 {@code Content} that need to be processed
         * @return HTTP content-related headers
         * @throws IOException Error while processing content
         * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
         * @see io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration HttpServerConfiguration
         * @see Content
         * @since v1.0.0
         */
        public static String processOutgoingContent(final HttpServerConfiguration httpServerConfiguration,
                                                    final Content content) throws IOException {
            if (content == null) return "Content-Length: 0\r\n";

            StringBuilder result = new StringBuilder();
            result.append("Content-Type: ").append(content.getContentType()).append("\r\n");

            if (content.isContentEncodingGzipped()) {
                result.append("Content-Encoding: ").append("gzip").append("\r\n");
                // InputStream gzip should be implemented
                switch (content.getData()) {
                    case Path path -> {
                        Path temp = Files.createTempFile(Paths.get(httpServerConfiguration.getTempDirectory()), "nexus-content-", null);
                        try (var fileInputStream = new FileInputStream(path.toFile());
                             var fileOutputStream = new FileOutputStream(temp.toFile());
                             var gzipOutputStream = new GZIPOutputStream(fileOutputStream, httpServerConfiguration.getMaxChunkSize())) {
                            int c;
                            byte[] buffer = new byte[httpServerConfiguration.getMaxChunkSize()];
                            while ((c = fileInputStream.read(buffer)) != -1) {
                                gzipOutputStream.write(buffer, 0, c);
                            }
                            gzipOutputStream.flush();
                            content.setData(temp);
                        }
                    }
                    case byte[] bytes -> {
                        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream, httpServerConfiguration.getMaxChunkSize())) {
                            gzipOutputStream.write(bytes);
                            gzipOutputStream.flush();
                            content.setData(byteArrayOutputStream.toByteArray());
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + content.getData());
                }
            }

            if (!content.isTransferEncodingChunked()) {
                switch (content.getData()) {
                    case Path path -> result.append("Content-Length: ").append(Files.size(path)).append("\r\n");
                    case byte[] bytes -> result.append("Content-Length: ").append(bytes.length).append("\r\n");
                    case InputStream ignored -> {}
                    default -> throw new IllegalStateException("Unexpected value: " + content.getData());
                }
            } else {
                result.append("Transfer-Encoding: ").append("chunked").append("\r\n");
            }

            if (content.getDownloadName() != null) {
                result.append("Content-Disposition: attachment; filename=\"").append(content.getDownloadName()).append("\"\r\n");
            }

            return result.toString();
        }

        /**
         * Write pre-processed content data to the socket output stream.
         *
         * @param requestConsumer {@code RequestConsumer}
         * @param content         Pre-processed {@code Content}
         * @throws IOException Error while writing data to the socket output stream
         * @see #processOutgoingContent(HttpServerConfiguration, Content)
         * @see RequestConsumer
         * @see Content
         * @since v1.0.0
         */
        public static void writeContent(final RequestConsumer requestConsumer,
                                        final Content content) throws IOException {
            try (InputStream inputStream = switch (content.getData()) {
                case Path path -> new FileInputStream(path.toFile());
                case byte[] bytes -> new ByteArrayInputStream(bytes);
                case InputStream stream -> stream;
                default -> throw new IllegalStateException("Unexpected value: " + content.getData());
            }) {
                int c;
                byte[] buffer = new byte[requestConsumer.getHttpServerConfiguration().getMaxChunkSize()];
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
                    requestConsumer.getSocket().getOutputStream().write("0\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                    requestConsumer.getSocket().getOutputStream().flush();
                }
            }
        }
    }
}
