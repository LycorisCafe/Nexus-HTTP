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

package io.github.lycoriscafe.nexus.http.core;

import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;

import java.lang.annotation.*;

/**
 * Mark class as an HTTP endpoints holding class.
 * <pre>
 *     {@code
 *     @HttpEndpoint
 *     public class HttpEndpointsClass {
 *          @GET("/sampleGetEndpoint")
 *          public static HttpResponse sampleGetEndpoint(HttpGetRequest request,
 *                                                       HttpResponse response) {
 *              // The real endpoint will http://hsot:port/sampleGetEndpoint
 *              // ...
 *              return response;
 *          }
 *     }
 *     }
 *     {@code
 *     @HttpEndpoint("/api/v1.0.0")
 *     public class HttpEndpointsClass {
 *          @GET("/sampleGetEndpoint")
 *          public static HttpResponse sampleGetEndpoint(HttpGetRequest request,
 *                                                       HttpResponse response) {
 *              // The real endpoint will http://hsot:port/api/v1.0.0/sampleGetEndpoint
 *              // ...
 *              return response;
 *          }
 *     }
 *     }
 * </pre>
 *
 * @apiNote For more settings, please take a look at {@code HttpServerConfiguration}.
 * @see io.github.lycoriscafe.nexus.http.core.requestMethods.annotations
 * @see HttpServerConfiguration#setUrlPrefix(String)
 * @since v1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpEndpoint {
    /**
     * @return Extended endpoint value
     * @see HttpEndpoint
     * @since v1.0.0
     */
    String value() default "";
}
