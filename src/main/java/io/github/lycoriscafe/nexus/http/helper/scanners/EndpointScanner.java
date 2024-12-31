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

package io.github.lycoriscafe.nexus.http.helper.scanners;

import io.github.lycoriscafe.nexus.http.core.HttpEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.auth.AuthScheme;
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authenticated;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.content.ExpectContent;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.*;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import io.github.lycoriscafe.nexus.http.helper.util.LogFormatter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * Method endpoint scanner.
 *
 * @see ReqEndpoint
 * @see Database
 * @since v1.0.0
 */
public final class EndpointScanner {
    private static final Logger logger = LoggerFactory.getLogger(EndpointScanner.class);

    /**
     * Scan for available {@code HttpEndpoint} authenticated classes and it's annotated method. Then pass them into the database.
     *
     * @param serverConfiguration {@code HttpServerConfiguration} instance bound to the server
     * @param database            {@code Database} instance bound to the server
     * @throws SQLException     Error while writing data to the database
     * @throws ScannerException Error while scanning for the endpoints
     * @see HttpServerConfiguration
     * @see Database
     * @see EndpointScanner
     * @since v1.0.0
     */
    public static void scan(final HttpServerConfiguration serverConfiguration,
                            final Database database) throws SQLException, ScannerException {
        LogFormatter.log(logger.atDebug(), "Begin endpoint scanning");
        Reflections reflections = new Reflections(serverConfiguration.getBasePackage());
        Set<Class<?>> classes = reflections.get(SubTypes.of(TypesAnnotated.with(HttpEndpoint.class)).asClass());
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getMethods()) {
                AuthScheme authSchemeAnnotation = null;
                boolean authenticated = clazz.isAnnotationPresent(Authenticated.class) || method.isAnnotationPresent(Authenticated.class);
                if (serverConfiguration.getDefaultAuthentications() == null && authenticated) {
                    throw new ScannerException("Authenticated endpoint found but no default authentications provided");
                }

                String endpointValue;
                HttpRequestMethod reqMethod;
                switch (method) {
                    case Method m when m.isAnnotationPresent(GET.class) && Modifier.isStatic(m.getModifiers()) -> {
                        if (m.isAnnotationPresent(ExpectContent.class)) {
                            throw new ScannerException("@ExpectContent on GET endpoint - " + clazz.getName() + "#" + method.getName());
                        }
                        endpointValue = m.getAnnotation(GET.class).value();
                        reqMethod = HttpRequestMethod.GET;
                    }
                    case Method m when m.isAnnotationPresent(POST.class) && Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(POST.class).value();
                        reqMethod = HttpRequestMethod.POST;
                    }
                    case Method m when m.isAnnotationPresent(PUT.class) && Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(PUT.class).value();
                        reqMethod = HttpRequestMethod.PUT;
                    }
                    case Method m when m.isAnnotationPresent(DELETE.class) && Modifier.isStatic(m.getModifiers()) -> {
                        if (m.isAnnotationPresent(ExpectContent.class)) {
                            throw new ScannerException("@ExpectContent on DELETE endpoint - " + clazz.getName() + "#" + method.getName());
                        }
                        endpointValue = m.getAnnotation(DELETE.class).value();
                        reqMethod = HttpRequestMethod.DELETE;
                    }
                    case Method m when m.isAnnotationPresent(PATCH.class) && Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(PATCH.class).value();
                        reqMethod = HttpRequestMethod.PATCH;
                    }
                    case Method m when m.isAnnotationPresent(HEAD.class) && Modifier.isStatic(m.getModifiers()) -> {
                        if (m.isAnnotationPresent(ExpectContent.class)) {
                            throw new ScannerException("@ExpectContent on HEAD endpoint - " + clazz.getName() + "#" + method.getName());
                        }
                        endpointValue = m.getAnnotation(HEAD.class).value();
                        reqMethod = HttpRequestMethod.HEAD;
                    }
                    case Method m when m.isAnnotationPresent(OPTIONS.class) && Modifier.isStatic(m.getModifiers()) -> {
                        if (m.isAnnotationPresent(ExpectContent.class)) {
                            throw new ScannerException("@ExpectContent on OPTIONS endpoint - " + clazz.getName() + "#" + method.getName());
                        }
                        endpointValue = m.getAnnotation(OPTIONS.class).value();
                        reqMethod = HttpRequestMethod.OPTIONS;
                    }
                    case Method m when m.isAnnotationPresent(BearerEndpoint.class) && Modifier.isStatic(m.getModifiers()) -> {
                        if (m.isAnnotationPresent(ExpectContent.class) &&
                                !m.getAnnotation(ExpectContent.class).value().equals("application/x-www-form-urlencoded")) {
                            throw new ScannerException("@ExpectContent on @BearerEndpoint endpoint with unexpected value " +
                                    "(possible value: application/x-www-form-urlencoded) - " + clazz.getName() + "#" + method.getName());
                        }
                        endpointValue = m.getAnnotation(BearerEndpoint.class).value().value();
                        reqMethod = HttpRequestMethod.POST;
                        authSchemeAnnotation = AuthScheme.BEARER;
                    }
                    default -> {
                        continue;
                    }
                }

                String endpointUri = serverConfiguration.getUrlPrefix() + "/" + clazz.getAnnotation(HttpEndpoint.class).value() + "/" + endpointValue;
                database.addEndpointData(new ReqEndpoint(endpointUri, reqMethod, authenticated, clazz, method, authSchemeAnnotation));
                LogFormatter.log(logger.atDebug(), "Endpoint found (" + reqMethod + ") @ " + clazz.getName() + "#" + method.getName());
            }
        }
        LogFormatter.log(logger.atDebug(), "End endpoint scanning");
    }
}
