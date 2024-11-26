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

package io.github.lycoriscafe.nexus.http.helper.scanners;

import io.github.lycoriscafe.nexus.http.core.HttpEndpoint;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.*;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.core.statusCodes.annotations.*;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.models.ReqEndpoint;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public final class EndpointScanner {
    public static void scan(final HttpServerConfiguration serverConfiguration,
                            final Database database)
            throws SQLException, ScannerException {
        Reflections reflections = new Reflections(serverConfiguration.getBasePackage());
        Set<Class<?>> classes = reflections.get(SubTypes.of(TypesAnnotated.with(HttpEndpoint.class)).asClass());
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getMethods()) {
                String endpointValue = null;
                HttpRequestMethod reqMethod = switch (method) {
                    case Method m when m.isAnnotationPresent(GET.class) &&
                            Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(GET.class).value();
                        yield HttpRequestMethod.GET;
                    }
                    case Method m when m.isAnnotationPresent(POST.class) &&
                            Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(POST.class).value();
                        yield HttpRequestMethod.POST;
                    }
                    case Method m when m.isAnnotationPresent(PUT.class) &&
                            Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(PUT.class).value();
                        yield HttpRequestMethod.PUT;
                    }
                    case Method m when m.isAnnotationPresent(DELETE.class) &&
                            Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(DELETE.class).value();
                        yield HttpRequestMethod.DELETE;
                    }
                    case Method m when m.isAnnotationPresent(PATCH.class) &&
                            Modifier.isStatic(m.getModifiers()) -> {
                        endpointValue = m.getAnnotation(PATCH.class).value();
                        yield HttpRequestMethod.PATCH;
                    }
                    default -> null;
                };

                if (reqMethod == null) {
                    continue;
                } else {
                    if (endpointValue == null) {
                        throw new ScannerException("null endpoint at : " +
                                "class - " + clazz.getName() + " , " +
                                "method - " + method.getName());
                    }
                }

                String statusAnnotationValue = null;
                HttpStatusCode statusAnnotation = switch (method) {
                    case Method m when m.isAnnotationPresent(Gone.class) -> HttpStatusCode.GONE;
                    case Method m when m.isAnnotationPresent(MovedPermanently.class) -> {
                        statusAnnotationValue = m.getAnnotation(MovedPermanently.class).value();
                        yield HttpStatusCode.MOVED_PERMANENTLY;
                    }
                    case Method m when m.isAnnotationPresent(MovedTemporarily.class) -> {
                        statusAnnotationValue = m.getAnnotation(MovedTemporarily.class).value();
                        yield HttpStatusCode.FOUND;
                    }
                    case Method m when m.isAnnotationPresent(NotImplemented.class) -> {
                        statusAnnotationValue = m.getAnnotation(NotImplemented.class).value();
                        yield HttpStatusCode.NOT_IMPLEMENTED;
                    }
                    case Method m when m.isAnnotationPresent(PermanentRedirect.class) -> {
                        statusAnnotationValue = m.getAnnotation(PermanentRedirect.class).value();
                        yield HttpStatusCode.PERMANENT_REDIRECT;
                    }
                    case Method m when m.isAnnotationPresent(TemporaryRedirect.class) -> {
                        statusAnnotationValue = m.getAnnotation(TemporaryRedirect.class).value();
                        yield HttpStatusCode.TEMPORARY_REDIRECT;
                    }
                    case Method m when m.isAnnotationPresent(UnavailableForLegalReasons.class) -> {
                        statusAnnotationValue = m.getAnnotation(UnavailableForLegalReasons.class).value();
                        yield HttpStatusCode.UNAVAILABLE_FOR_LEGAL_REASONS;
                    }
                    default -> null;
                };

                database.addEndpointData(new ReqEndpoint(
                                (serverConfiguration.isIgnoreEndpointCases() ?
                                        clazz.getAnnotation(HttpEndpoint.class).value().toLowerCase(Locale.US) :
                                        clazz.getAnnotation(HttpEndpoint.class).value()) +
                                        (serverConfiguration.isIgnoreEndpointCases() ?
                                                endpointValue.toLowerCase(Locale.US) : endpointValue),
                                reqMethod, clazz, method, statusAnnotation,
                                serverConfiguration.isIgnoreEndpointCases() ?
                                        statusAnnotationValue == null ?
                                                null : statusAnnotationValue.toLowerCase(Locale.US) :
                                        statusAnnotationValue
                        )
                );
            }
        }
    }
}
