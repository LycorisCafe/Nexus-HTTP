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
import java.util.*;

public sealed class HttpRequest permits HttpGetRequest, HttpPostRequest {
    private final RequestConsumer requestConsumer;
    private boolean suspendProcessing;

    private final long requestId;
    private final HttpRequestMethod requestMethod;
    private final String endpoint;
    private Map<String, String> parameters;
    private List<Header> headers;
    private List<Cookie> cookies;

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

    public void suspendProcessing() {
        suspendProcessing = true;
    }

    public void setHeaders(final Header... headers) {
        if (cookies == null || headers.length == 0) {
            return;
        }

        if (this.headers == null) {
            this.headers = new ArrayList<>();
        }
        this.headers.addAll(Arrays.asList(headers));
    }

    public void setCookies(final Cookie... cookies) {
        if (cookies == null || cookies.length == 0) {
            return;
        }

        if (this.cookies == null) {
            this.cookies = new ArrayList<>();
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

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void finalizeRequest() {
        if (suspendProcessing) {
            return;
        }

        try {
            ReqEndpoint endpointDetails = requestConsumer.getDatabase().getEndpointData(this);
            if (endpointDetails == null) {
                requestConsumer.dropConnection(HttpStatusCode.NOT_FOUND);
                return;
            }

            if (endpointDetails.getStatusAnnotation() != null) {
                processStatusAnnotation(endpointDetails);
                return;
            }

            requestConsumer.send((HttpResponse) endpointDetails.getMethod().invoke(null, this));
        } catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            requestConsumer.dropConnection(HttpStatusCode.INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void processStatusAnnotation(final ReqEndpoint reqEndpoint) {
        // TODO Handle status annotations with response
    }
}
