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

package io.github.lycoriscafe.nexus.http.core.headers.auth;

import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;

import java.lang.annotation.*;

/**
 * Mark endpoint as an authenticated resource. To access the endpoint, the {@code Authorization} header must present in the request.
 * <p>
 * The endpoint annotated with this,
 * <ul>
 *     <li>must {@code public} and {@code static}</li>
 *     <li>must annotate with any of request method annotation</li>
 *     <li>must return {@code HttpResponse} as <b>return</b> value</li>
 *     <li>must accept any type of {@code HttpRequest} as <b>only parameter</b></li>
 * </ul>
 * <pre>
 *     {@code
 *     @Authenticated
 *     @GET("/sampleAuthenticatedEndpoint")
 *     public static HttpResponse sampleAuthenticatedEndpoint(HttpGetRequest request) {
 *         HttpResponse response = ...
 *         // Either these methods will work
 *         // if (request.getAuthorization() instanceof AuthScheme.BASIC) {}
 *         if (request.getAuthorization().getAuthScheme() == AuthScheme.BASIC) {
 *             var authorization = (BasicAuthorization) request.getAuthorization();
 *             // validations ...
 *         }
 *         // ...
 *         return response;
 *     }
 *     }
 * </pre>
 *
 * @see HttpResponse
 * @see HttpRequest
 * @see Authorization
 * @see AuthScheme
 * @see <a href="https://datatracker.ietf.org/doc/rfc7235">Hypertext Transfer Protocol (HTTP/1.1): Authentication (rfc 7235)</a>
 * @since v1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Authenticated {
}
