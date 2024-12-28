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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.util.Objects;

/**
 * When a client requests to generate a {@code Bearer} access token to a resource and if failed, instance of this class will be returned from the
 * target endpoint. The endpoint must be annotated with {@code @BearerEndpoint}.
 * <pre>
 *     {@code
 *     // Example code
 *     var tokenResponse = new BearerTokenFailResponse("INVALID_REQUEST");
 *     }
 * </pre>
 *
 * @see BearerEndpoint
 * @see BearerTokenResponse
 * @see <a href="https://datatracker.ietf.org/doc/rfc6749">The OAuth 2.0 Authorization Framework (rfc6749)</a>
 * @since v1.0.0
 */
public final class BearerTokenFailResponse implements BearerTokenResponse {
    private final BearerTokenRequestError error;
    private String errorDescription;
    private String errorUri;

    /**
     * Create an instance of {@code BearerTokenFailResponse}.
     *
     * @param error Requested token error
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    public BearerTokenFailResponse(final BearerTokenRequestError error) {
        this.error = Objects.requireNonNull(error);
    }

    /**
     * Get provided error.
     *
     * @return Provided error
     * @see #BearerTokenFailResponse(BearerTokenRequestError)
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    public BearerTokenRequestError getError() {
        return error;
    }

    /**
     * Get provided error description.
     *
     * @return Error description
     * @see #setErrorDescription(String)
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Set error description.
     *
     * @param errorDescription Error description
     * @return Same {@code BearerTokenRequestError} instance
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    public BearerTokenFailResponse setErrorDescription(String errorDescription) {
        this.errorDescription = Objects.requireNonNull(errorDescription);
        return this;
    }

    /**
     * Get provided error URI.
     *
     * @return Error URI
     * @see #setErrorUri(String)
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    public String getErrorUri() {
        return errorUri;
    }

    /**
     * Set error URI.
     *
     * @param errorUri Error URI
     * @return Same {@code BearerTokenRequestError} instance
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    public BearerTokenFailResponse setErrorUri(String errorUri) {
        this.errorUri = Objects.requireNonNull(errorUri);
        return this;
    }

    /**
     * Process token response as {@code HttpResponse}.
     *
     * @param requestId       Target client's request id
     * @param requestConsumer Target client's {@code RequestConsumer}
     * @return New instance of {@code HttpResponse} with processed content
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BearerTokenFailResponse
     * @since v1.0.0
     */
    @Override
    public HttpResponse parse(long requestId,
                              RequestConsumer requestConsumer) {
        return new HttpResponse(requestId, requestConsumer).setStatusCode(HttpStatusCode.BAD_REQUEST)
                .setContent(new Content("application/json", "{\"error\":\"" + error.getValue() + "\"" +
                        (getErrorDescription() != null ? ",\"error_description\":\"" + getErrorDescription() + "\"" : "") +
                        (getErrorUri() != null ? "\"error_uri\":\"" + getErrorUri() + "\"" : "") + "}"));
    }
}
