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

import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;

import java.lang.annotation.*;

/**
 * Mark endpoint as a 'Bearer' token generator endpoint.
 * <p>
 * The endpoint annotated with this,
 * <ul>
 *     <li>must {@code public} and {@code static}</li>
 *     <li>must return {@code BearerTokenResponse} as <b>return</b> value</li>
 *     <li>must accept {@code BearerTokenRequest} as <b>only parameter</b></li>
 * </ul>
 * <pre>
 *     {@code
 *     @BearerEndpoint(@POST("/sampleBearerTokenEndpoint"))
 *     public static BearerTokenResponse sampleBearerTokenEndpoint(BearerTokenRequest request,
 *                                                                 BearerTokenResponse response) {
 *         // ...
 *         return response;
 *     }
 *     }
 * </pre>
 *
 * @see BearerTokenResponse
 * @see BearerTokenRequest
 * @see POST
 * @see <a href="https://datatracker.ietf.org/doc/rfc6750">The OAuth 2.0 Authorization Framework: Bearer Token Usage (rfc6750)</a>
 * @since v1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BearerEndpoint {
    /**
     * Request endpoint value.
     *
     * @return Endpoint value
     * @see BearerEndpoint
     * @since v1.0.0
     */
    POST value();
}
