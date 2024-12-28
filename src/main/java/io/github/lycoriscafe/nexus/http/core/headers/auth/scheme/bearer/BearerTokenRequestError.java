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

/**
 * Possible errors for 'Brear' token request request.
 *
 * @see BearerTokenRequest
 * @see <a href="https://datatracker.ietf.org/doc/rfc6749">The OAuth 2.0 Authorization Framework (rfc6749)</a>
 * @since v1.0.0
 */
public enum BearerTokenRequestError {
    /**
     * The request is missing a required parameter, includes an unsupported parameter value (other than grant type), repeats a parameter, includes
     * multiple credentials, utilizes more than one mechanism for authenticating the client, or is otherwise malformed.
     *
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    INVALID_REQUEST("invalid_request"),
    /**
     * Client authentication failed (e.g., unknown client, no client authentication included, or unsupported authentication method).  The
     * authorization server MAY return an HTTP 401 (Unauthorized) status code to indicate which HTTP authentication schemes are supported.  If the
     * client attempted to authenticate via the "Authorization" request header field, the authorization server MUST respond with an HTTP 401
     * (Unauthorized) status code and include the "WWW-Authenticate" response header field matching the authentication scheme used by the client.
     *
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    INVALID_CLIENT("invalid_client"),
    /**
     * The provided authorization grant (e.g., authorization code, resource owner credentials) or refresh token is invalid, expired, revoked, does not
     * match the redirection URI used in the authorization request, or was issued to another client.
     *
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    INVALID_GRANT("invalid_grant"),
    /**
     * The authenticated client is not authorized to use this authorization grant type.
     *
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    /**
     * The authorization grant type is not supported by the authorization server.
     *
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),
    /**
     * The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner.
     *
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    INVALID_SCOPE("invalid_scope");

    private final String value;

    BearerTokenRequestError(String value) {
        this.value = value;
    }

    /**
     * Get the target 'BearerTokenRequestError' value to set in the header.
     *
     * @return 'BearerTokenRequestError' value to set in the header
     * @see BearerTokenRequestError
     * @since v1.0.0
     */
    public String getValue() {
        return value;
    }
}
