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

package io.github.lycoriscafe.nexus.http.engine;

import io.github.lycoriscafe.nexus.http.HttpServer;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.util.LogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Base of the server. This class does the basic request receiving and response sending operations. Every receiving connection will have an
 * instance of this class. Also, instance of this class will be able to handle {@code Long.MAX_VALUE - 1} times of requests in keep-alive connection.
 * Connection related settings can be found at {@code HttpServerConfiguration}.
 *
 * @see HttpServerConfiguration
 * @see Long#MAX_VALUE
 * @since v1.0.0
 */
public final class RequestConsumer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestConsumer.class);

    private static final byte[] lineTerminator = "\r\n".getBytes(StandardCharsets.UTF_8);
    private final RequestProcessor requestProcessor;

    private final HttpServerConfiguration serverConfiguration;
    private final Database database;
    private final Socket socket;

    // readLine() components
    private final byte[] terminatePoint = new byte[2];
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private final SortedMap<Long, HttpResponse> responseQue;
    private long requestId = 0L;
    private long responseId = 0L;

    /**
     * Create an instance of the {@code RequestConsumer}.
     *
     * @param serverConfiguration {@code HttpServerConfiguration} passed to {@code HttpServer}.
     * @param database            {@code Database} initialized by {@code HttpServer}
     * @param socket              {@code Socket} initialized by {@code HttpServer}
     * @throws IOException Error while setting socket timeout
     * @apiNote Connection timeout specified by {@code HttpServerConfiguration} will handle in here.
     * @see HttpServer
     * @see HttpServerConfiguration
     * @see RequestConsumer
     * @since v1.0.0
     */
    public RequestConsumer(final HttpServerConfiguration serverConfiguration,
                           final Database database,
                           final Socket socket) throws IOException {
        requestProcessor = new RequestProcessor(this);

        this.serverConfiguration = Objects.requireNonNull(serverConfiguration);
        this.database = Objects.requireNonNull(database);
        this.socket = Objects.requireNonNull(socket);

        this.socket.setSoTimeout(serverConfiguration.getConnectionTimeout());
        responseQue = new TreeMap<>();
    }

    /**
     * Calculate and return a unique request identifier for this socket connection. This method can generate {@code 0} to {@code Long.MAX_VALUE} times
     * of ids.
     *
     * @return Unique request identifier
     * @see Long#MAX_VALUE
     * @see RequestConsumer
     * @since v1.0.0
     */
    private long getRequestId() {
        if (requestId == Long.MAX_VALUE) return -1L;
        if (requestId + 2 == Long.MAX_VALUE) {
            LogFormatter.log(logger.atDebug(), "Maximum request id capped, requesting to drop connection as last response");
            dropConnection(Long.MAX_VALUE, HttpStatusCode.INTERNAL_SERVER_ERROR, "connection reset", logger);
        }
        return requestId++;
    }

    /**
     * Get server based {@code HttpServerConfiguration}.
     *
     * @return Server based {@code HttpServerConfiguration}
     * @see HttpServerConfiguration
     * @see RequestConsumer
     * @since v1.0.0
     */
    public HttpServerConfiguration getHttpServerConfiguration() {
        return serverConfiguration;
    }

    /**
     * Get server based {@code Database}.
     *
     * @return Server based {@code Database}
     * @see Database
     * @see RequestConsumer
     * @since v1.0.0
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Get initialized socket for this connection by {@code HttpServer}.
     *
     * @return Initialized socket.
     * @see Socket
     * @see HttpServer
     * @see RequestConsumer
     * @since v1.0.0
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Method to read character stream with direct socket input stream. The point is efficient and speed reading of bytes and convert into string. The
     * line terminator always will {@code \r\n}.
     *
     * @return String, read using socket input stream and converted (charset UTF-8)
     * @throws IOException Error while reading data from the socket input stream
     * @see RequestConsumer
     * @since v1.0.0
     */
    public String readLine() throws IOException {
        byteArrayOutputStream.reset();

        int c = socket.getInputStream().read(terminatePoint, 0, 2);
        if (c != 2) return null;

        while (!Arrays.equals(lineTerminator, terminatePoint)) {
            int b = socket.getInputStream().read();
            if (b == -1) return null;
            byteArrayOutputStream.write(terminatePoint[0]);
            if (byteArrayOutputStream.size() > serverConfiguration.getMaxHeaderSize()) {
                dropConnection(0, HttpStatusCode.CONTENT_TOO_LARGE, "provided header too large", logger);
                return null;
            }
            terminatePoint[0] = terminatePoint[1];
            terminatePoint[1] = (byte) b;
        }

        return byteArrayOutputStream.size() == 0 ? "" : byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    /**
     * Base method to read request data, decode and pass into the {@code RequestProcessor}.
     *
     * @see RequestProcessor
     * @see RequestConsumer
     * @since v1.0.0
     */
    @Override
    public void run() {
        LogFormatter.log(logger.atTrace(), "Client connection received - LocalIP:" + socket.getInetAddress().getHostAddress());
        Thread.currentThread().setName("RequestConsumer@" + socket.getInetAddress().getHostAddress());
        try {
            String requestLine = null;
            List<String> headers = new ArrayList<>();

            while (true) {
                String line = readLine();
                if (line == null) break;
                line = line.trim();

                if (requestLine == null) {
                    requestLine = line;
                    continue;
                }

                if (line.isEmpty()) {
                    requestProcessor.process(getRequestId(), requestLine, headers);
                    requestLine = null;
                    headers.clear();
                    continue;
                }

                if (headers.size() > serverConfiguration.getMaxHeadersPerRequest()) {
                    // Handle max headers count exceeded
                    dropConnection(requestId, HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "request header fields count too large", logger);
                    return;
                }

                headers.add(line);
            }
        } catch (IOException e) {
            LogFormatter.log(logger.atDebug(), "Client connection terminated - " + e.getMessage());
        }
    }

    /**
     * Prepare an HTTP response to drop the connection. This method is used to error reporting in-API exception occurred. The {@code exception}
     * settings can be found in {@code HttpServerConfiguration}.
     *
     * @param requestId      Request id bound to the request that throws an exception
     * @param httpStatusCode HTTP status code
     * @param exception      Detailed exception message
     * @see HttpServerConfiguration#setAddErrorMessageToResponseHeaders(boolean)
     * @see RequestConsumer
     * @since v1.0.0
     */
    public void dropConnection(final long requestId,
                               final HttpStatusCode httpStatusCode,
                               final String exception,
                               final Logger logger) {

        LogFormatter.log(logger.atDebug(), "Connection drop - RequestId:" + requestId + ", StatusCode:" + httpStatusCode + ", Exception:" + exception);
        var httpResponse = new HttpResponse(requestId, this).setStatusCode(httpStatusCode).setDropConnection(true);
        if (getHttpServerConfiguration().isAddErrorMessageToResponseHeaders() && exception != null) {
            httpResponse.setContent(new Content("application/json", "{\"exception\":\"" + exception + "\"}"));
        }
        send(httpResponse);
    }

    /**
     * Base response writer method. Response headers are written to the socket output stream by this method, but content related write operations are
     * handled by the {@code Content} class. This method is constructed to support {@code HTTP Pipelining}.
     *
     * @param httpResponse {@code HttpResponse} that should be sent
     * @see Content.WriteOperations#writeContent(RequestConsumer, Content)
     * @see RequestConsumer
     * @since v1.0.0
     */
    public synchronized void send(final HttpResponse httpResponse) {
        if (socket.isClosed()) return;
        responseQue.put(httpResponse.getRequestId(), httpResponse);
        LogFormatter.log(logger.atTrace(), "HttpResponse added to the queue - RequestId:" + httpResponse.getRequestId());

        List<Long> keySet = responseQue.keySet().stream().toList();
        for (Long key : keySet) {
            if (key > responseId) break;

            HttpResponse response = responseQue.get(key);
            if (response.getRequestId() == responseId) {
                try {
                    OutputStream outputStream = socket.getOutputStream();

                    String headers = response.finalizeResponse();
                    if (headers == null) return;
                    outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();

                    if (response.getContent() != null) Content.WriteOperations.writeContent(this, response.getContent());

                    if (response.isDropConnection()) {
                        socket.close();
                        logger.atTrace().log("NEXUS-HTTP :: Connection dropped as per dropRequest()");
                    }
                    LogFormatter.log(logger.atTrace(), "HttpResponse sent - RequestId:" + httpResponse.getRequestId());
                } catch (IOException e) {
                    LogFormatter.log(logger.atDebug(), e.getMessage());
                }

                responseQue.remove(key);
                responseId++;
            }
        }
    }
}
