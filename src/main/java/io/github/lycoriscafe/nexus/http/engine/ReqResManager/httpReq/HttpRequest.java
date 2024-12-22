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
import io.github.lycoriscafe.nexus.http.core.headers.cors.CORSRequest;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import io.github.lycoriscafe.nexus.http.helper.models.ReqFile;
import io.github.lycoriscafe.nexus.http.helper.models.ReqMaster;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Parent HTTP request class.
 *
 * @see HttpDeleteRequest
 * @see HttpGetRequest
 * @see HttpHeadRequest
 * @see HttpOptionsRequest
 * @see HttpPatchRequest
 * @see HttpPostRequest
 * @see HttpPutRequest
 * @see io.github.lycoriscafe.nexus.http.core.requestMethods.annotations
 * @since v1.0.0
 */
public sealed class HttpRequest permits HttpGetRequest, HttpPostRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestConsumer requestConsumer;
    private final long requestId;
    private final HttpRequestMethod requestMethod;
    private String endpoint;
    private Map<String, String> parameters;
    private List<Header> headers;
    private List<Cookie> cookies;
    private CORSRequest corsRequest;
    private Authorization authorization;

    /**
     * @param requestConsumer {@code RequestConsumer} of incoming HTTP request
     * @param requestId       Unique identification for HTTP request
     * @param requestMethod   HTTP request method
     * @see HttpRequest
     * @see RequestConsumer
     * @since v1.0.0
     */
    public HttpRequest(final RequestConsumer requestConsumer,
                       final long requestId,
                       final HttpRequestMethod requestMethod) {
        this.requestConsumer = requestConsumer;
        this.requestId = requestId;
        this.requestMethod = requestMethod;
    }

    /**
     * Get {@code RequestConsumer} of incoming HTTP request
     *
     * @return {@code RequestConsumer}
     * @see RequestConsumer
     * @see HttpRequest
     * @since v1.0.0
     */
    public RequestConsumer getRequestConsumer() {
        return requestConsumer;
    }

    /**
     * Get unique identifier for this request based on {@code RequestConsumer}. It means when a connection received, the id will begin to cont from 1
     * to Long.MAX_VALUE (if hit, connection reset).
     *
     * @return Unique identifier for this request
     * @see RequestConsumer
     * @see HttpRequest
     * @since v1.0.0
     */
    public long getRequestId() {
        return requestId;
    }

    /**
     * Get request method of the incoming HTTP request.
     *
     * @return HTTP request method
     * @see HttpRequestMethod
     * @see HttpRequest
     * @since v1.0.0
     */
    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Set endpoint URI of the incoming request.
     *
     * @param endpoint Endpoint URI
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see HttpRequest
     * @since v1.0.0
     */
    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Get endpoint URI of the incoming request.
     *
     * @return Endpoint URI
     * @see HttpRequest
     * @since v1.0.0
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Set URI parameters come along with the request.
     *
     * @param parameters URI parameters
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see HttpRequest
     * @since v1.0.0
     */
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get URI parameters come along with the request.
     *
     * @return URI parameters
     * @see HttpRequest
     * @since v1.0.0
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Set non-specific headers come along with the request.
     *
     * @param header Header
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Header
     * @see HttpRequest
     * @since v1.0.0
     */
    public void setHeader(final Header header) {
        if (headers == null) headers = new NonDuplicateList<>();
        headers.add(header);
    }

    /**
     * Get non-specific headers come along with the request.
     *
     * @return Headers
     * @see Header
     * @see HttpRequest
     * @since v1.0.0
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * Set cookies come along with the request.
     *
     * @param cookies Cookies
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Cookie
     * @see HttpRequest
     * @since v1.0.0
     */
    public void setCookies(final List<Cookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * Get cookies come along with the request.
     *
     * @return Cookies
     * @see Cookie
     * @see HttpRequest
     * @since v1.0.0
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * Set CORS come along with the request.
     *
     * @param corsRequest CORS
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see CORSRequest
     * @see HttpRequest
     * @since v1.0.0
     */
    public void setCorsRequest(final CORSRequest corsRequest) {
        this.corsRequest = corsRequest;
    }

    /**
     * Get CORS come along with the request.
     *
     * @return CORS
     * @see CORSRequest
     * @see HttpRequest
     * @since v1.0.0
     */
    public CORSRequest getCorsRequest() {
        return corsRequest;
    }

    /**
     * Set authorization come along with the request.
     *
     * @param authorization Authorization
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Authorization
     * @see HttpRequest
     * @since v1.0.0
     */
    public void setAuthorization(final Authorization authorization) {
        this.authorization = authorization;
    }

    /**
     * Get authorization come along with the request.
     *
     * @return Authorization
     * @see Authorization
     * @see HttpRequest
     * @since v1.0.0
     */
    public Authorization getAuthorization() {
        return authorization;
    }

    /**
     * Finalize the HTTP request. It means process errors and if no errors found, call the appropriate endpoint methods and vice versa. Child classes
     * of this class also overrides this method and process some HTTP request method related operations.
     *
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see HttpGetRequest#finalizeRequest()
     * @see HttpPostRequest#finalizeRequest()
     * @see HttpRequest
     * @see ReqMaster
     * @since v1.0.0
     */
    public void finalizeRequest() {
        String parsedEndpoint = ReqMaster.parseEndpoint(getEndpoint());
        if (!getEndpoint().equals(parsedEndpoint)) {
            getRequestConsumer().send(new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.PERMANENT_REDIRECT)
                    .addHeader(new Header("Location", parsedEndpoint)));
            return;
        }

        try {
            List<ReqMaster> data = requestConsumer.getDatabase().getEndpointData(this);
            if (data.isEmpty()) {
                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_FOUND, "endpoint not found", logger);
                return;
            }

            ReqMaster endpointDetails = null;
            for (ReqMaster reqMaster : data) {
                if (reqMaster.getReqMethod() == getRequestMethod()) {
                    endpointDetails = reqMaster;
                    break;
                }
            }

            if (endpointDetails == null) {
                getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.METHOD_NOT_ALLOWED, "request method not allowed", logger);
                return;
            }

            switch (endpointDetails) {
                case ReqEndpoint reqEndpoint -> {
                    if (reqEndpoint.getStatusAnnotation() != null) {
                        processStatusAnnotation(reqEndpoint);
                        return;
                    }

                    if (reqEndpoint.getAuthSchemeAnnotation() != null) {
                        processAuthAnnotation(reqEndpoint);
                        return;
                    }

                    if (reqEndpoint.isAuthenticated() && getAuthorization() == null) {
                        getRequestConsumer().send(new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.UNAUTHORIZED)
                                .setAuthentications(getRequestConsumer().getHttpServerConfiguration().getDefaultAuthentications()));
                        return;
                    }

                    Object response = reqEndpoint.getMethod().invoke(null, this, new HttpResponse(getRequestId(), getRequestConsumer()));
                    if (response instanceof HttpResponse httpResponse) {
                        getRequestConsumer().send(httpResponse);
                    } else {
                        getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR, "invalid http response provided", logger);
                    }
                }
                case ReqFile reqFile -> {
                    // TODO implement
                }
                default -> throw new IllegalStateException("Unexpected value: " + endpointDetails);
            }
        } catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR, "error while processing request/response", logger);
            throw new RuntimeException(e);
        }
    }

    /**
     * If any status annotation present for the target endpoint method, the {@code finalizeRequest()} call this method to further processing.
     *
     * @param reqEndpoint {@code ReqEndpoint}
     * @apiNote Only used for in-API tasks.
     * @see HttpRequest#finalizeRequest()
     * @see ReqEndpoint
     * @see HttpRequest
     * @see io.github.lycoriscafe.nexus.http.core.statusCodes.annotations
     * @since v1.0.0
     */
    private void processStatusAnnotation(final ReqEndpoint reqEndpoint) {
        getRequestConsumer().send(switch (reqEndpoint.getStatusAnnotation()) {
            case FOUND -> new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.FOUND)
                    .addHeader(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case GONE -> new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.GONE);
            case MOVED_PERMANENTLY -> new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.MOVED_PERMANENTLY)
                    .addHeader(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case PERMANENT_REDIRECT -> new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.PERMANENT_REDIRECT)
                    .addHeader(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case TEMPORARY_REDIRECT -> new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.TEMPORARY_REDIRECT)
                    .addHeader(new Header("Location", reqEndpoint.getStatusAnnotationValue()));
            case UNAVAILABLE_FOR_LEGAL_REASONS -> {
                var response = new HttpResponse(getRequestId(), getRequestConsumer()).setStatusCode(HttpStatusCode.UNAVAILABLE_FOR_LEGAL_REASONS);
                if (!reqEndpoint.getStatusAnnotationValue().isEmpty()) {
                    response.addHeader(new Header("Link", "<" + reqEndpoint.getStatusAnnotationValue() + ">; rel=\"blocked-by\""));
                }
                yield response;
            }
            default -> throw new IllegalStateException("Unexpected value: " + reqEndpoint.getStatusAnnotation());
        });
    }

    /**
     * If any authentication annotation (like {@code @BearerEndpoint}) present for the target endpoint method, the {@code finalizeRequest()} call this
     * method to further processing.
     *
     * @param reqEndpoint {@code ReqEndpoint}
     * @apiNote Only used for in-API tasks.
     * @see HttpRequest#finalizeRequest()
     * @see ReqEndpoint
     * @see HttpRequest
     * @since v1.0.0
     */
    private void processAuthAnnotation(final ReqEndpoint reqEndpoint) throws InvocationTargetException, IllegalAccessException {
        switch (reqEndpoint.getAuthSchemeAnnotation()) {
            case BEARER -> {
                HttpPostRequest request = (HttpPostRequest) this;
                if (request.getContent() == null || !request.getContent().getContentType().equals("application/x-www-form-urlencoded")) {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.BAD_REQUEST, "content type must be application/x-www-form-urlencoded", logger);
                    return;
                }

                BearerTokenRequest bearerTokenRequest = BearerTokenRequest.parse(request);
                if (bearerTokenRequest == null) return;

                Object response = reqEndpoint.getMethod().invoke(null, bearerTokenRequest, new BearerTokenResponse());
                if ((response instanceof BearerTokenResponse bearerTokenResponse) && bearerTokenResponse.getBearerToken() != null) {
                    getRequestConsumer().send(BearerTokenResponse.parse(bearerTokenResponse, getRequestId(), getRequestConsumer()));
                } else {
                    getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR, "invalid bearer response provided", logger);
                }
            }
            default -> getRequestConsumer().dropConnection(getRequestId(), HttpStatusCode.NOT_IMPLEMENTED, "auth scheme not implemented", logger);
        }
    }
}
