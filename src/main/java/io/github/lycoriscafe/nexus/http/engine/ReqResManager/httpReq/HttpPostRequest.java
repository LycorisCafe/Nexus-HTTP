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
import io.github.lycoriscafe.nexus.http.core.headers.content.MultiPartFormData;
import io.github.lycoriscafe.nexus.http.core.headers.content.TransferEncoding;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.util.HashSet;
import java.util.Locale;

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

    private HashSet<TransferEncoding> transferEncoding;
    private HashSet<ContentEncoding> contentEncoding;
    private Integer contentLength = null;

    @Override
    public void finalizeRequest() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-type")) {
                if (!getEncodings()) return;
                if (getContentLength(transferEncoding == null || transferEncoding.contains(TransferEncoding.CHUNKED))) {
                    return;
                }

                String value = header.getValue().toLowerCase(Locale.US);
                Content content = switch (value) {
                    case String x when x.startsWith("multipart/form-data") ->
                            MultiPartFormData.process(getRequestId(), getRequestConsumer(), transferEncoding,
                                    contentEncoding, contentLength, value.split(";")[1].trim().split("=")[1]);
                    case String x when x.startsWith("text/") ->
                            Content.ReadOperations.processText(getRequestId(), getRequestConsumer(), transferEncoding,
                                    contentEncoding, contentLength, value);
                    case "application/json", "application/xml" ->
                            Content.ReadOperations.processText(getRequestId(), getRequestConsumer(), transferEncoding,
                                    contentEncoding, contentLength, value);
                    case "application/x-www-form-urlencoded" ->
                            Content.ReadOperations.processXWWWFormUrlencoded(getRequestId(), getRequestConsumer(),
                                    transferEncoding, contentEncoding, contentLength);
                    default -> Content.ReadOperations.processCommonContentType(getRequestId(), getRequestConsumer(),
                            transferEncoding, contentEncoding, contentLength, value);
                };

                if (content == null) return;

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
                        transferEncoding = new HashSet<>();
                        for (String value : values) {
                            try {
                                transferEncoding.add(TransferEncoding.valueOf(value));
                            } catch (IllegalArgumentException e) {
                                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_IMPLEMENTED);
                                yield false;
                            }
                        }
                        yield true;
                    }
                    case "content-encoding" -> {
                        contentEncoding = new HashSet<>();
                        for (String value : values) {
                            try {
                                contentEncoding.add(ContentEncoding.valueOf(value));
                            } catch (IllegalArgumentException e) {
                                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_IMPLEMENTED);
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
                        getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.CONTENT_TOO_LARGE);
                        return false;
                    }

                    getHeaders().remove(header);
                    return true;
                } catch (NumberFormatException e) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST);
                    return false;
                }
            }
        }

        if (!optional) {
            getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.LENGTH_REQUIRED);
            return false;
        }

        return true;
    }
}
