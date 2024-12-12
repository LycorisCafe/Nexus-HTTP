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
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerTokenRequest;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerTokenResponse;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import io.github.lycoriscafe.nexus.http.helper.models.ReqFile;
import io.github.lycoriscafe.nexus.http.helper.models.ReqMaster;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public sealed class HttpRequest permits HttpGetRequest, HttpPostRequest {
    private final RequestConsumer requestConsumer;
    private final long requestId;
    private final HttpRequestMethod requestMethod;
    private String endpoint;
    private Map<String, String> parameters;
    private List<Header> headers;
    private List<Cookie> cookies;
    private Authorization authorization;

    public HttpRequest(final RequestConsumer requestConsumer,
                       final long requestId,
                       final HttpRequestMethod requestMethod) {
        this.requestConsumer = requestConsumer;
        this.requestId = requestId;
        this.requestMethod = requestMethod;
    }

    public RequestConsumer getRequestConsumer() {
        return requestConsumer;
    }

    public long getRequestId() {
        return requestId;
    }

    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setHeader(final Header header) {
        if (header == null) return;
        if (headers == null) headers = new NonDuplicateList<>();
        headers.add(header);
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setCookies(final Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) return;
        if (this.cookies == null) this.cookies = new NonDuplicateList<>();
        this.cookies.addAll(Arrays.asList(cookies));
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setAuthorization(final Authorization authorization) {
        this.authorization = authorization;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void finalizeRequest() {
        try {
            ReqMaster endpointDetails = requestConsumer.getDatabase().getEndpointData(this);
            if (endpointDetails == null) {
                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_FOUND, "endpoint not found");
                return;
            }
            if (endpointDetails.getReqMethod() != getRequestMethod()) {
                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.METHOD_NOT_ALLOWED,
                        "requested method not allowed");
                return;
            }

            switch (endpointDetails) {
                case ReqEndpoint reqEndpoint -> {
                    if (reqEndpoint.getAuthSchemeAnnotation() != null) {
                        processAuthAnnotation(reqEndpoint);
                        return;
                    }

                    if (reqEndpoint.isAuthenticated() && getAuthorization() == null) {
                        getRequestConsumer().send(new HttpResponse(getRequestId(), getRequestConsumer(),
                                HttpStatusCode.UNAUTHORIZED).setAuthentications(
                                getRequestConsumer().getServerConfiguration().getDefaultAuthentications()));
                        return;
                    }

                    if (reqEndpoint.getStatusAnnotation() != null) {
                        processStatusAnnotation(reqEndpoint);
                        return;
                    }

                    Object response = reqEndpoint.getMethod().invoke(null, this);
                    if (response instanceof HttpResponse httpResponse) {
                        getRequestConsumer().send(httpResponse);
                    } else {
                        getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR,
                                "invalid http response provided");
                    }
                }
                case ReqFile reqFile -> {
                    // TODO implement
                }
                default -> throw new IllegalStateException("Unexpected value: " + endpointDetails);
            }
        } catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR,
                    "error while processing request/response");
            throw new RuntimeException(e);
        }
    }

    private void processStatusAnnotation(final ReqEndpoint reqEndpoint) {
        getRequestConsumer().send(switch (reqEndpoint.getStatusAnnotation()) {
            case FOUND -> new HttpResponse(getRequestId(), getRequestConsumer(), HttpStatusCode.FOUND).setHeader(
                    new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case GONE -> new HttpResponse(getRequestId(), getRequestConsumer(), HttpStatusCode.GONE);
            case MOVED_PERMANENTLY ->
                    new HttpResponse(getRequestId(), getRequestConsumer(), HttpStatusCode.MOVED_PERMANENTLY).setHeader(
                            new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case PERMANENT_REDIRECT ->
                    new HttpResponse(getRequestId(), getRequestConsumer(), HttpStatusCode.PERMANENT_REDIRECT).setHeader(
                            new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case TEMPORARY_REDIRECT ->
                    new HttpResponse(getRequestId(), getRequestConsumer(), HttpStatusCode.TEMPORARY_REDIRECT).setHeader(
                            new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case UNAVAILABLE_FOR_LEGAL_REASONS -> new HttpResponse(getRequestId(), getRequestConsumer(),
                    HttpStatusCode.UNAVAILABLE_FOR_LEGAL_REASONS).setHeader(
                    new Header("Link", reqEndpoint.getStatusAnnotationValue() + "; rel=\"blocked-by\""));
            default -> throw new IllegalStateException("Unexpected value: " + reqEndpoint.getStatusAnnotation());
        });
    }

    private void processAuthAnnotation(final ReqEndpoint reqEndpoint)
            throws InvocationTargetException, IllegalAccessException {
        switch (reqEndpoint.getAuthSchemeAnnotation()) {
            case Bearer -> {
                if (getRequestMethod() != HttpRequestMethod.POST) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST,
                            "request method must be POST");
                    return;
                }

                HttpPostRequest request = (HttpPostRequest) this;
                if (request.getContent() == null ||
                        !request.getContent().getContentType().equals("application/x-www-form-urlencoded")) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST,
                            "content type must be application/x-www-form-urlencoded");
                    return;
                }

                BearerTokenRequest bearerTokenRequest = BearerTokenRequest.parse(request);
                if (bearerTokenRequest == null) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST,
                            "missing required parameter");
                    return;
                }

                Object response = reqEndpoint.getMethod().invoke(null, bearerTokenRequest);
                if (response instanceof BearerTokenResponse httpResponse) {
                    getRequestConsumer().send(
                            BearerTokenResponse.parse(httpResponse, getRequestId(), getRequestConsumer()));
                } else {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR,
                            "invalid bearer response provided");
                }
            }
            default -> getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_IMPLEMENTED,
                    "auth scheme not implemented");
        }
    }
}
