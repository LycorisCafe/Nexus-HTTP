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

package io.github.lycoriscafe.nexus.http.core.requestMethods.annotations;

import io.github.lycoriscafe.nexus.http.core.HttpEndpoint;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq.HttpPatchRequest;
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes.HttpResponse;

import java.lang.annotation.*;

/**
 * Mark endpoint method as an HTTP {@code PATCH} request method endpoint. There are a few points that API users should follow.
 * <ul>
 *     <li>Annotated method must be {@code public} and {@code static}.</li>
 *     <li>Annotated method must use return type as {@code HttpResponse}.</li>
 *     <li>Annotated method must have two parameters. {@code HttpPatchRequest} and {@code HttpResponse} respectively.</li>
 *     <li>The class holding the annotated method must be annotated with {@code HttpEndpoint}.</li>
 * </ul>
 * When doing the in-method processing, it's recommended to return the same {@code HttpResponse} that got as a method parameter with any changes (for
 * more info, see {@code HttpResponse} class).
 * <pre>
 *     {@code
 *     @PATCH("/samplePatchEndpoint")
 *     public static HttpResponse samplePatchEndpoint(HttpPatchRequest request,
 *                                                    HttpResponse response) {
 *         // ...
 *         return response;
 *     }
 *     }
 * </pre>
 *
 * @apiNote Make sure to change the HTTP status code appropriately when returning the {@code HttpResponse}. The default value is {@code 200 OK}.
 * @see HttpPatchRequest
 * @see HttpResponse
 * @see HttpStatusCode
 * @see HttpResponse#setStatusCode(HttpStatusCode)
 * @see io.github.lycoriscafe.nexus.http.core.requestMethods.annotations
 * @see HttpEndpoint
 * @since v1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PATCH {
    /**
     * @return Request endpoint value
     * @see PATCH
     * @since v1.0.0
     */
    String value();
}
