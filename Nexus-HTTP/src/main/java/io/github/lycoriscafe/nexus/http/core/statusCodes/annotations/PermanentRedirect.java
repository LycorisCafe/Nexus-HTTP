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

package io.github.lycoriscafe.nexus.http.core.statusCodes.annotations;

import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;

import java.lang.annotation.*;

/**
 * Mark endpoint method as an HTTP redirect endpoint. The redirect response HTTP status code will {@code 308 Permanent Redirect}. The point is when
 * API user's API changed, user can just annotate it with this and ignore the deep in-method response processing part (if this annotation presents,
 * the method will not execute). To use this annotation, one of the request method annotation must be presented.
 * <pre>
 *     {@code
 *     @PermanentRedirect("/redirectEndpoint")
 *     @GET("/sampleGetEndpoint")
 *     public static HttpResponse sampleGetEndpoint(HttpGetRequest request,
 *                                                  HttpResponse response) {
 *         // method will not execute ...
 *         return response;
 *     }
 *     }
 * </pre>
 *
 * @see HttpStatusCode
 * @see io.github.lycoriscafe.nexus.http.core.statusCodes.annotations
 * @see io.github.lycoriscafe.nexus.http.core.requestMethods.annotations
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/308">308 Permanent Redirect (MDN Docs)</a>
 * @since v1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermanentRedirect {
    /**
     * @return Redirect URI
     * @see PermanentRedirect
     * @since v1.0.0
     */
    String value();
}
