/*
 * Copyright 2024 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.core.headers.cors;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.util.List;
import java.util.Locale;

/**
 * Cross-Origin Resource Sharing (CORS) for HTTP requests.
 * <pre>
 *     {@code
 *     // Example code
 *     // 'httpRequest' is an endpoint parameter (HttpGetRequest, ...)
 *     var corsRequest = httpRequest.getCorsRequest();
 *     }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">Cross-Origin Resource Sharing (MDN Docs)</a>
 * @since v1.0.0
 */
public final class CORSRequest {
    private String origin;
    private HttpRequestMethod accessControlRequestMethod;
    private List<String> accessControlRequestHeaders;

    /**
     * Create instance of {@code CORSRequest}.
     *
     * @see CORSRequest
     * @since v1.0.0
     */
    public CORSRequest() {}

    /**
     * Get CORS {@code Origin}.
     *
     * @return Origin
     * @see CORSRequest
     * @since v1.0.0
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Set CORS {@code Origin}
     *
     * @param origin Origin
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see CORSRequest
     * @since v1.0.0
     */
    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    /**
     * Get CORS {@code Access-Control-Request-Method}.
     *
     * @return Request method
     * @see CORSRequest
     * @since v1.0.0
     */
    public HttpRequestMethod getAccessControlRequestMethod() {
        return accessControlRequestMethod;
    }

    /**
     * Set CORS {@code Access-Control-Request-Method}
     *
     * @param accessControlRequestMethod Request method
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see CORSRequest
     * @since v1.0.0
     */
    public void setAccessControlRequestMethod(final HttpRequestMethod accessControlRequestMethod) {
        this.accessControlRequestMethod = accessControlRequestMethod;
    }

    /**
     * Get CORS {@code Access-Control-Request-Headers}.
     *
     * @return {@code List} of request headers
     * @see CORSRequest
     * @since v1.0.0
     */
    public List<String> getAccessControlRequestHeaders() {
        return accessControlRequestHeaders;
    }

    /**
     * Add a value to CORS {@code Access-Control-Request-Headers}.
     *
     * @param accessControlRequestHeader Request header
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see CORSRequest
     * @since v1.0.0
     */
    public void addAccessControlRequestHeader(final String accessControlRequestHeader) {
        if (accessControlRequestHeaders == null) accessControlRequestHeaders = new NonDuplicateList<>();
        accessControlRequestHeaders.add(accessControlRequestHeader);
    }

    /**
     * Process incoming CORS header to {@code CORSRequest} instance.
     *
     * @param request Temporary {@code CORSRequest} hold in {@code RequestProcessor}
     * @param values  Full CORS request header split to key and value pair
     * @return New or existing {@code CORSRequest}
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see io.github.lycoriscafe.nexus.http.engine.RequestProcessor RequestProcessor
     * @see CORSRequest
     * @since v1.0.0
     */
    public static CORSRequest processIncomingCors(CORSRequest request,
                                                  final String[] values) {
        if (request == null) request = new CORSRequest();
        switch (values[0].toLowerCase(Locale.US)) {
            case "origin" -> request.setOrigin(values[1].trim());
            case "access-control-request-method" -> request.setAccessControlRequestMethod(HttpRequestMethod.validate(values[1].trim()));
            case "access-control-request-headers" -> {
                String[] headers = values[1].split(",", 0);
                for (String header : headers) {
                    request.addAccessControlRequestHeader(header.trim());
                }
            }
        }
        return request;
    }
}
