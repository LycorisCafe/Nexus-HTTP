/*
 * Copyright 2025 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.core.headers.content.UrlEncodedData;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * When a client requests to generate a {@code Bearer} access token to a resource, instance of this class will be received to the target endpoint. The
 * endpoint must be annotated with {@code @BearerEndpoint}.
 *
 * @see BearerEndpoint
 * @see <a href="https://datatracker.ietf.org/doc/rfc6750">The OAuth 2.0 Authorization Framework: Bearer Token Usage (rfc6750)</a>
 * @since v1.0.0
 */
public final class BearerTokenRequest {
    private static final Logger logger = LoggerFactory.getLogger(BearerTokenRequest.class);

    private final String grantType;
    private Map<String, String> params;

    /**
     * Create an instance of {@code BearerTokenRequest}.
     *
     * @param grantType Token grant type
     * @see BearerTokenRequest
     * @since v1.0.0
     */
    public BearerTokenRequest(final String grantType) {
        this.grantType = grantType;
    }

    /**
     * Get provided token grant type.
     *
     * @return Token grant type
     * @see BearerTokenRequest
     * @since v1.0.0
     */
    public String getGrantType() {
        return grantType;
    }

    /**
     * Get other parameters come along with the token request (without {@code grant_type}).
     *
     * @return Other parameters
     * @see #setParams(Map)
     * @see BearerTokenRequest
     * @since v1.0.0
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Set other parameters come along with the token request (without {@code grant_type})
     *
     * @param params Other parameters
     * @return Same {@code BearerTokenRequest} instance
     * @see BearerTokenRequest
     * @since v1.0.0
     */
    public BearerTokenRequest setParams(final Map<String, String> params) {
        this.params = params;
        return this;
    }

    /**
     * Process provided request content to a new instance of {@code BearerTokenRequest}.
     *
     * @param request {@code HttpPost} request
     * @return New instance of {@code BearerTokenRequest}
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BearerTokenRequest
     * @since v1.0.0
     */
    public static BearerTokenRequest parse(final HttpPostRequest request) {
        UrlEncodedData params;
        if (!(request.getContent().getData() instanceof UrlEncodedData)) {
            request.getRequestConsumer()
                    .dropConnection(request.getRequestId(), HttpStatusCode.BAD_REQUEST, "invalid content type", logger);
            return null;
        }
        params = (UrlEncodedData) request.getContent().getData();

        if (!params.containsKey("grant_type")) {
            request.getRequestConsumer().dropConnection(request.getRequestId(), HttpStatusCode.BAD_REQUEST, "grant_type missing", logger);
            return null;
        }

        BearerTokenRequest bearerTokenRequest = new BearerTokenRequest(params.get("grant_type"));
        params.remove("grant_type");
        return bearerTokenRequest.setParams(params);
    }
}
