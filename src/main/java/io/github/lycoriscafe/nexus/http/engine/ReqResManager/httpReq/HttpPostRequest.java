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

import io.github.lycoriscafe.nexus.http.core.headers.content.*;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public sealed class HttpPostRequest extends HttpRequest permits HttpPatchRequest, HttpPutRequest {
    private Content content;

    public HttpPostRequest(final RequestConsumer requestConsumer,
                           final long requestId,
                           final HttpRequestMethod requestMethod) {
        super(requestConsumer, requestId, requestMethod);
    }

    public Content getContent() {
        return content;
    }

    private List<TransferEncoding> transferEncoding;
    private List<ContentEncoding> contentEncoding;
    private Integer contentLength = null;

    @Override
    public void finalizeRequest() {
        for (int i = 0; i < getHeaders().size(); i++) {
            if (getHeaders().get(i).getName().equalsIgnoreCase("content-type")) {
                if (!getEncodings()) return;
                if (!getContentLength(
                        transferEncoding != null && transferEncoding.contains(TransferEncoding.CHUNKED))) {
                    return;
                }

                String value = getHeaders().get(i).getValue().toLowerCase(Locale.US);
                try {
                    content = switch (value) {
                        case String x when x.startsWith("multipart/form-data") -> MultiPartFormData.process(getRequestId(), getRequestConsumer(),
                                value.split(";")[1].split("=")[1], contentLength, transferEncoding,
                                contentEncoding);
                        case "application/x-www-form-urlencoded" -> UrlEncodedData.process(getRequestId(), getRequestConsumer(), contentLength,
                                transferEncoding, contentEncoding);
                        default -> Content.ReadOperations.process(getRequestId(), getRequestConsumer(), value,
                                contentLength, transferEncoding, contentEncoding);
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
                getHeaders().remove(getHeaders().get(i));

                return switch (headerName) {
                    case "transfer-encoding" -> {
                        transferEncoding = new NonDuplicateList<>();
                        for (String value : values) {
                            try {
                                transferEncoding.add(TransferEncoding.valueOf(value));
                            } catch (IllegalArgumentException e) {
                                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_IMPLEMENTED,
                                        "provided transfer encoding not supported");
                                yield false;
                            }
                        }
                        yield true;
                    }
                    case "content-encoding" -> {
                        contentEncoding = new NonDuplicateList<>();
                        for (String value : values) {
                            try {
                                contentEncoding.add(ContentEncoding.valueOf(value));
                            } catch (IllegalArgumentException e) {
                                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_IMPLEMENTED,
                                        "provided content encoding not supported");
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
        for (int i = 0; i < getHeaders().size(); i++) {
            if (getHeaders().get(i).getName().equalsIgnoreCase("content-length")) {
                try {
                    contentLength = Integer.parseInt(getHeaders().get(i).getValue());

                    if (contentLength > getRequestConsumer().getServerConfiguration().getMaxContentLength()) {
                        getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.CONTENT_TOO_LARGE,
                                "content too large");
                        return false;
                    }

                    getHeaders().remove(getHeaders().get(i));
                    return true;
                } catch (NumberFormatException e) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST,
                            "invalid content length");
                    return false;
                }
            }
        }

        if (!optional) {
            getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.LENGTH_REQUIRED,
                    "content length required");
            return false;
        }

        return true;
    }
}
