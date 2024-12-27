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

package io.github.lycoriscafe.nexus.http.core.headers.content;

import java.lang.annotation.*;

/**
 * Mark annotated endpoint expects a provided type of content. This annotation can only be used with HTTP request methods {@code POST}, {@code PATCH},
 * and {@code PUT}. If the condition not satisfied, HTTP error {@code 422 Unprocessable Content} will be sent. There are three types of content
 * values.
 * <ul>
 *     <li>{@code any} - Expect anu type of content (default)</li>
 *     <li>{@code none} - Expect no content</li>
 *     <li>any other type that needs to be compared</li>
 * </ul>
 *
 * <pre>
 *     {@code
 *     @POST("/simpleEndpoint")
 *     @ExpectContent("multipart/form-data")
 *     public static HttpResponse simpleEndpoint(HttpPostRequest request,
 *                                               HttpResponse response) {
 *         // ...
 *         return response;
 *     }
 *     }
 * </pre>
 *
 * @see Content
 * @since v1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpectContent {
    String value() default "any";
}
