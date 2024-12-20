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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication;

/**
 * The <code>Bearer</code> authentication for HTTP. An instance of this class will send to the clients when they asked to access to a protected
 * resource without specifying <code>Authorization</code> header.
 * <pre>
 *     {@code
 *      <!-- General header format with realm specified -->
 *      HTTP/1.1 401 Unauthorized
 *      WWW-Authenticate: Bearer realm="specifiedRealm"
 *
 *      <!-- General header format with error specified -->
 *      HTTP/1.1 401 Unauthorized
 *      WWW-Authenticate: Bearer error="invalid_token"
 *
 *      <!-- General header format -->
 *      HTTP/1.1 401 Unauthorized
 *      WWW-Authenticate: Bearer realm="example", error="invalid_token", error_description="The access token expired"
 *      }
 *      {@code
 *      // Example codes
 *      var bearerAuth1 = new BearerAuthentication("specifiedRealm");
 *      var bearerAuth2 = new BearerAuthentication(BearerError.INVALID_TOKEN);
 *      var bearerAuth3 = new BearerAuthentication(BearerError.INVALID_TOKEN)
 *          .setErrorDescription("The access token expired");
 *      }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication Authentication
 * @see io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization Authorization
 * @see BearerAuthorization
 * @see <a href="https://datatracker.ietf.org/doc/rfc6750">The OAuth 2.0 Authorization Framework: Bearer Token Usage (rfc6750)</a>
 * @since v1.0.0
 */
public final class BearerAuthentication extends Authentication {
    private BearerError error;
    private String realm;
    private String scope;
    private String errorDescription;
    private String errorURI;

    /**
     * Create an instance of <code>BearerAuthentication</code> with specifying authentication error.
     *
     * @param error Authentication error
     * @see BearerError
     * @see #BearerAuthentication(String)
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public BearerAuthentication(final BearerError error) {
        this.error = error;
    }

    /**
     * Create an instance of <code>BearerAuthentication</code> with specifying realm.
     *
     * @param realm Realm for the target resource
     * @see #BearerAuthentication(BearerError)
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public BearerAuthentication(final String realm) {
        this.realm = realm;
    }

    /**
     * Get authentication error of the provided instance.
     *
     * @return Specified authentication error
     * @see #setError(BearerError)
     * @see BearerError
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public BearerError getError() {
        return error;
    }

    /**
     * Set authentication error for the provided instance.
     *
     * @param error Authentication error
     * @return Same <code>BearerAuthentication</code> instance
     * @apiNote if you specify the authentication error with the constructor, reusing this method will change the provided values.
     * @see BearerError
     * @see BearerAuthentication
     * @see #BearerAuthentication(BearerError)
     * @since v1.0.0
     */
    public BearerAuthentication setError(final BearerError error) {
        this.error = error;
        return this;
    }

    /**
     * Get realm of the provided instance.
     *
     * @return Specified realm
     * @see #setRealm(String)
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Set realm for the provided instance.
     *
     * @param realm Realm
     * @return Same <code>BearerAuthentication</code> instance
     * @apiNote if you specify the <code>Bearer</code> realm with the constructor, reusing this method will change the provided values.
     * @see BearerAuthentication
     * @see #BearerAuthentication(String)
     * @since v1.0.0
     */
    public BearerAuthentication setRealm(final String realm) {
        this.realm = realm;
        return this;
    }

    /**
     * Get scope of the provided instance.
     *
     * @return Specified scope
     * @see #setScope(String)
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set scope for the provided instance.
     *
     * @param scope Scope
     * @return Same <code>BearerAuthentication</code> instance
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public BearerAuthentication setScope(final String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Get error description of the provided instance.
     *
     * @return Specified error description
     * @see #setErrorDescription(String)
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Set <code>Bearer</code> error description for the provided instance.
     *
     * @param errorDescription Error description
     * @return Same <code>BearerAuthentication</code> instance
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public BearerAuthentication setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }

    /**
     * Get error URI of the provided instance.
     *
     * @return Specified Error URI
     * @see #setErrorURI(String)
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public String getErrorURI() {
        return errorURI;
    }

    /**
     * Set error URI for the provided instance.
     *
     * @param errorURI Error URI
     * @return Same <code>BearerAuthentication</code> instance
     * @see BearerAuthentication
     * @since v1.0.0
     */
    public BearerAuthentication setErrorURI(final String errorURI) {
        this.errorURI = errorURI;
        return this;
    }

    /**
     * Process authentication instance as <code>WWW-Authenticate</code> HTTP header value.
     *
     * @return HTTP header value string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see BearerAuthentication
     * @since v1.0.0
     */
    @Override
    public String processOutgoingAuth() {
        StringBuilder output = new StringBuilder().append("Bearer ");
        boolean errorFound = false;
        if (error != null) {
            errorFound = true;
            output.append("error=\"").append(error.getValue()).append("\"");
        }

        if (errorFound) {
            output.append(", realm=\"").append(realm).append("\"");
        } else {
            output.append("realm=\"").append(realm).append("\"");
        }

        if (scope != null) output.append(", scope=\"").append(scope).append("\"");
        if (errorDescription != null) output.append(", error-description=\"").append(errorDescription).append("\"");
        if (errorURI != null) output.append(", error-uri=\"").append(errorURI).append("\"");
        return output.toString();
    }
}
