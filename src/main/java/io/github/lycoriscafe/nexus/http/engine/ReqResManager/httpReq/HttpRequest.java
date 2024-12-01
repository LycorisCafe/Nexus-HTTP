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
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public sealed class HttpRequest permits HttpGetRequest, HttpPostRequest {
    private final RequestConsumer requestConsumer;

    private final long requestId;
    private final HttpRequestMethod requestMethod;
    private final String endpoint;
    private Map<String, String> parameters;
    private HashSet<Header> headers;
    private HashSet<Cookie> cookies;

    public HttpRequest(final RequestConsumer requestConsumer,
                       final long requestId,
                       final HttpRequestMethod requestMethod,
                       final String endpoint) {
        this.requestConsumer = requestConsumer;

        this.requestId = requestId;
        this.requestMethod = requestMethod;

        String[] endpointParts = endpoint.split("\\?", 2);
        this.endpoint = endpointParts[0];

        if (endpointParts.length > 1) {
            parameters = new HashMap<>();
            for (String param : endpointParts[1].split("&", 0)) {
                String[] keyValue = param.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public RequestConsumer getRequestConsumer() {
        return requestConsumer;
    }

    public void setHeaders(final Header... headers) {
        if (cookies == null || headers.length == 0) {
            return;
        }

        if (this.headers == null) {
            this.headers = new HashSet<>();
        }
        this.headers.addAll(Arrays.asList(headers));
    }

    public void setCookies(final Cookie... cookies) {
        if (cookies == null || cookies.length == 0) {
            return;
        }

        if (this.cookies == null) {
            this.cookies = new HashSet<>();
        }
        this.cookies.addAll(Arrays.asList(cookies));
    }

    public long getRequestId() {
        return requestId;
    }

    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public HashSet<Header> getHeaders() {
        return headers;
    }

    public HashSet<Cookie> getCookies() {
        return cookies;
    }

    public void finalizeRequest() {
        try {
            ReqEndpoint endpointDetails = requestConsumer.getDatabase().getEndpointData(this);
            if (endpointDetails == null) {
                requestConsumer.dropConnection(requestId, HttpStatusCode.NOT_FOUND);
                return;
            }

            if (endpointDetails.getStatusAnnotation() != null) {
                processStatusAnnotation(endpointDetails);
                return;
            }

            requestConsumer.send((HttpResponse) endpointDetails.getMethod().invoke(null, this));
        } catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            requestConsumer.dropConnection(requestId, HttpStatusCode.INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void processStatusAnnotation(final ReqEndpoint reqEndpoint) {
        requestConsumer.send(switch (reqEndpoint.getStatusAnnotation()) {
            case FOUND -> new HttpResponse(requestId, getRequestConsumer(), HttpStatusCode.FOUND)
                    .header(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case GONE -> new HttpResponse(requestId, getRequestConsumer(), HttpStatusCode.GONE);
            case MOVED_PERMANENTLY ->
                    new HttpResponse(requestId, getRequestConsumer(), HttpStatusCode.MOVED_PERMANENTLY)
                            .header(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case PERMANENT_REDIRECT ->
                    new HttpResponse(requestId, getRequestConsumer(), HttpStatusCode.PERMANENT_REDIRECT)
                            .header(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case TEMPORARY_REDIRECT ->
                    new HttpResponse(requestId, getRequestConsumer(), HttpStatusCode.TEMPORARY_REDIRECT)
                            .header(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case UNAVAILABLE_FOR_LEGAL_REASONS ->
                    new HttpResponse(requestId, getRequestConsumer(), HttpStatusCode.UNAVAILABLE_FOR_LEGAL_REASONS)
                            .header(new Header("Link",
                                    reqEndpoint.getStatusAnnotationValue() + "; rel=\"blocked-by\""));
            default -> throw new IllegalStateException("Unexpected value: " + reqEndpoint.getStatusAnnotation());
        });
    }
}
