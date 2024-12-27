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

package io.github.lycoriscafe.nexus.http.core.requestMethods;

/**
 * HTTP request methods that server can support.
 *
 * @see <a href="https://datatracker.ietf.org/doc/rfc9110">HTTP Semantics (rfc 9110)</a>
 * @since v1.0.0
 */
public enum HttpRequestMethod {
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    DELETE,
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    GET,
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    HEAD,
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    OPTIONS,
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    PATCH,
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    POST,
    /**
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    PUT;

    /**
     * Validate if the passed {@code HttpRequestMethod} can be supported by the server.
     *
     * @param requestMethod {@code HttpRequestMethod} string
     * @return If supported, parsed {@code HttpRequestMethod} else null
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see HttpRequestMethod
     * @since v1.0.0
     */
    public static HttpRequestMethod validate(String requestMethod) {
        try {
            return HttpRequestMethod.valueOf(requestMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
