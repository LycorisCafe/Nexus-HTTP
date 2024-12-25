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

import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.headers.content.MultipartFormData;
import io.github.lycoriscafe.nexus.http.core.headers.content.UrlEncodedData;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * HTTP POST request method.
 *
 * @see POST
 * @see HttpRequest
 * @since v1.0.0
 */
public sealed class HttpPostRequest extends HttpRequest permits HttpPatchRequest, HttpPutRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpPostRequest.class);

    private Content content;

    /**
     * @param requestConsumer {@code RequestConsumer} bound to the HTTP request
     * @param requestId       Request id bound to the HTTP request
     * @param requestMethod   HTTP request method of the request
     * @see HttpPostRequest
     * @since v1.0.0
     */
    public HttpPostRequest(final RequestConsumer requestConsumer,
                           final long requestId,
                           final HttpRequestMethod requestMethod) {
        super(requestConsumer, requestId, requestMethod);
    }

    /**
     * Get request content.
     *
     * @return Received {@code Content}
     * @see Content
     * @see HttpRequest
     * @since v1.0.0
     */
    public Content getContent() {
        return content;
    }

    private boolean chunked;
    private boolean gzipped;
    private Integer contentLength = null;

    /**
     * Process content reading-related operations.
     *
     * @see Content.ReadOperations
     * @since v1.0.0
     */
    @Override
    public void finalizeRequest() {
        for (int i = 0; i < getHeaders().size(); i++) {
            if (getHeaders().get(i).getName().equalsIgnoreCase("content-type")) {
                if (!getEncodings()) return;
                if (!chunked) if (!getContentLength()) return;

                String value = getHeaders().get(i).getValue().toLowerCase(Locale.US).trim();
                try {
                    content = switch (value) {
                        case String x when x.startsWith("multipart/form-data") -> {
                            String[] headerParts = x.split(";", 0);
                            if (headerParts.length != 2 || !headerParts[1].trim().startsWith("boundary")) {
                                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST, "Invalid content-type", logger);
                                yield null;
                            }
                            yield MultipartFormData.process(getRequestId(), getRequestConsumer(), "--" + headerParts[1].split("=", 2)[1],
                                    contentLength, chunked, gzipped);
                        }
                        case "application/x-www-form-urlencoded" -> UrlEncodedData.process(getRequestId(), getRequestConsumer(), contentLength,
                                chunked, gzipped);
                        default -> Content.ReadOperations.process(getRequestId(), getRequestConsumer(), value, contentLength, chunked, gzipped);
                    };
                    if (content == null) return;
                } catch (IOException e) {
                    return;
                }

                getHeaders().remove(getHeaders().get(i));
                break;
            }
        }

        super.finalizeRequest();
    }

    private boolean getEncodings() {
        for (int i = 0; i < getHeaders().size(); i++) {
            String headerName = getHeaders().get(i).getName().toLowerCase(Locale.US);
            if (headerName.equals("transfer-encoding") || headerName.equals("content-encoding")) {
                String[] values = getHeaders().get(i).getValue().toLowerCase(Locale.US).split(",", 0);
                switch (headerName) {
                    case "transfer-encoding" -> {
                        if (values.length == 1 && values[0].equals("chunked")) {
                            chunked = true;
                        } else {
                            getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST, "only chunked transfer encoding supported", logger);
                            return false;
                        }
                    }
                    case "content-encoding" -> {
                        if (values.length == 1 && values[0].equals("gzip")) {
                            gzipped = true;
                        } else {
                            getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST, "only gzip content encoding supported", logger);
                            return false;
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + headerName);
                }
                getHeaders().remove(getHeaders().get(i));
            }
        }
        return true;
    }

    private boolean getContentLength() {
        for (int i = 0; i < getHeaders().size(); i++) {
            if (getHeaders().get(i).getName().equalsIgnoreCase("content-length")) {
                try {
                    contentLength = Integer.parseInt(getHeaders().get(i).getValue());
                    if (contentLength > getRequestConsumer().getHttpServerConfiguration().getMaxContentLength()) {
                        getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.CONTENT_TOO_LARGE, "content too large", logger);
                        return false;
                    }
                    getHeaders().remove(getHeaders().get(i));
                    return true;
                } catch (NumberFormatException e) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST, "invalid content length", logger);
                    return false;
                }
            }
        }

        getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.LENGTH_REQUIRED, "content length required", logger);
        return false;
    }
}
