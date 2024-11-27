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
import io.github.lycoriscafe.nexus.http.core.headers.content.Encoding;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public sealed class HttpPostRequest extends HttpRequest
        permits HttpPatchRequest, HttpPutRequest {
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

    private Encoding contentEncoding;
    private Long contentLength;

    @Override
    public void finalizeRequest() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-type")) {
                contentEncoding = getContentEncoding();
                if (contentEncoding == null) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return;
                }

                contentLength = getContentLength();
                if (contentLength == null) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
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

    private Encoding getContentEncoding() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-encoding")) {
                String value = header.getValue().toLowerCase(Locale.US);
                getHeaders().remove(header);
                return value.equals("gzip") ? Encoding.GZIP :
                        value.equals("chunked") ? Encoding.CHUNKED : null;
            }
        }
        return Encoding.NONE;
    }

    private Long getContentLength() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-length")) {
                try {
                    Long value = Long.parseLong(header.getValue());
                    getHeaders().remove(header);
                    return value;
                } catch (NumberFormatException e) {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return null;
                }
            }
        }
        getRequestConsumer().dropConnection(HttpStatusCode.LENGTH_REQUIRED);
        return null;
    }

    private void processMultiPartFormData(String boundary) {
        if (getContentEncoding() == Encoding.CHUNKED) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            suspendProcessing();
        }
    }

    private void processXWWWFormUrlencoded() {
        Map<String, String> parameters = new HashMap<>();
        // TODO process
        content = new Content<>("application/x-www-form-urlencoded", contentLength, contentEncoding, parameters);
    }

    private void processText(String contentType) {
        // TODO process
    }

    private void processDefault(String contentType) {
        // TODO process
    }
}
