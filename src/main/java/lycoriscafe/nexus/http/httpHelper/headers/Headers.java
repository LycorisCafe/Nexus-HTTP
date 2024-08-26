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

package lycoriscafe.nexus.http.httpHelper.headers;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Headers {
    String[] accept() default "*/*";

    String[] acceptCH() default "";

    String acceptCharset() default "UTF-8";

    String[] acceptEncoding() default "*";

    String[] acceptLanguage() default "*";

    String[] acceptPatch() default "*/*";

    String[] acceptPost() default "*/*";

    String acceptRanges() default "none";

    boolean accessControlAllowCredentials() default false;

    String[] accessControlAllowHeaders() default "Accept";

    String[] accessControlAllowMethods() default "GET";

    String accessControlAllowOrigin() default "*";

    String[] accessControlExposeHeaders() default "*";

    int accessControlMaxAge() default 5;

    String[] accessControlRequestHeaders() default "";

    String accessControlRequestMethod() default "GET";

    int age() default 0;

    String[] allow() default "GET";
}
