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
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    private ContentEncoding contentEncoding;
    private Integer contentLength;

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

    private ContentEncoding getContentEncoding() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-contentEncoding")) {
                String value = header.getValue().toLowerCase(Locale.US);
                getHeaders().remove(header);
                return value.equals("gzip") ? ContentEncoding.GZIP :
                        value.equals("chunked") ? ContentEncoding.CHUNKED : null;
            }
        }
        return ContentEncoding.NONE;
    }

    private Integer getContentLength() {
        for (Header header : getHeaders()) {
            if (header.getName().equalsIgnoreCase("content-length")) {
                try {
                    Integer value = Integer.parseInt(header.getValue());
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
        if (getContentEncoding() == ContentEncoding.CHUNKED) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            suspendProcessing();
        }
    }

    private void processXWWWFormUrlencoded() {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            switch (contentEncoding) {
                case CHUNKED -> {
                    getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
                    return;
                }
                case NONE ->
                        byteArrayOutputStream = readContent(getRequestConsumer().getSocket().getInputStream(), contentLength);
                case GZIP ->
                        byteArrayOutputStream = readGzipContent(getRequestConsumer().getSocket().getInputStream(), contentLength);
            }
        } catch (IOException e) {
            getRequestConsumer().dropConnection(HttpStatusCode.BAD_REQUEST);
            suspendProcessing();
            return;
        }

        if (byteArrayOutputStream == null || byteArrayOutputStream.size() < contentLength) {
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

//    private static Path readChunkedContent()
}
