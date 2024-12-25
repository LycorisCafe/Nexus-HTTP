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

package io.github.lycoriscafe.nexus.http.helper.models;

import io.github.lycoriscafe.nexus.http.core.headers.auth.AuthScheme;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.scanners.EndpointScanner;

import java.lang.reflect.Method;

/**
 * Method endpoint model for communicate endpoint data to/from endpoint methods to/from the database.
 *
 * @see EndpointScanner
 * @see ReqMaster
 * @see Database
 * @since v1.0.0
 */
public final class ReqEndpoint extends ReqMaster {
    private final Class<?> className;
    private final Method methodName;
    private final HttpStatusCode statusAnnotation;
    private final String statusAnnotationValue;
    private final AuthScheme authSchemeAnnotation;

    /**
     * Create instance of {@code ReqEndpoint}.
     *
     * @param requestEndpoint       Endpoint URI
     * @param reqMethod             HTTP request method
     * @param authenticated         Is the endpoint authenticated?
     * @param className             Target class
     * @param methodName            Target method
     * @param statusAnnotation      Available status annotation
     * @param statusAnnotationValue Available status annotation value
     * @param authSchemeAnnotation  Available authentication scheme annotation
     * @see ReqEndpoint
     * @since v1.0.0
     */
    public ReqEndpoint(final String requestEndpoint,
                       final HttpRequestMethod reqMethod,
                       final boolean authenticated,
                       final Class<?> className,
                       final Method methodName,
                       final HttpStatusCode statusAnnotation,
                       final String statusAnnotationValue,
                       final AuthScheme authSchemeAnnotation) {
        super(requestEndpoint, reqMethod, authenticated);
        this.className = className;
        this.methodName = methodName;
        this.statusAnnotation = statusAnnotation;
        this.statusAnnotationValue = statusAnnotationValue;
        this.authSchemeAnnotation = authSchemeAnnotation;
    }

    /**
     * Get provided target class.
     *
     * @return Target class.
     * @see ReqEndpoint
     * @since v1.0.0
     */
    public Class<?> getClazz() {
        return className;
    }

    /**
     * Get provided target method.
     *
     * @return Target method
     * @see ReqEndpoint
     * @since v1.0.0
     */
    public Method getMethod() {
        return methodName;
    }

    /**
     * Get provided available status annotation.
     *
     * @return Available status annotation
     * @see ReqEndpoint
     * @since v1.0.0
     */
    public HttpStatusCode getStatusAnnotation() {
        return statusAnnotation;
    }

    /**
     * Get provided available status annotation value.
     *
     * @return Available status annotation value
     * @see ReqEndpoint
     * @since v1.0.0
     */
    public String getStatusAnnotationValue() {
        return statusAnnotationValue;
    }

    /**
     * Get provided available authentication scheme annotation.
     *
     * @return Available authentication scheme annotation
     * @see ReqEndpoint
     * @since v1.0.0
     */
    public AuthScheme getAuthSchemeAnnotation() {
        return authSchemeAnnotation;
    }
}
