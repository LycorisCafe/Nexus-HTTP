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

package io.github.lycoriscafe.nexus.http.configuration;

import io.github.lycoriscafe.nexus.http.core.HTTPEndpoint;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.DELETE;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.PUT;
import io.github.lycoriscafe.nexus.http.core.statusCodes.annotations.*;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public final class AnnotationScanner {
    public static void scan(final Connection DATABASE,
                            final String BASE_PACKAGE) throws SQLException {
        Reflections reflections = new Reflections(BASE_PACKAGE);
        Set<Class<?>> classes = reflections.get(SubTypes.of(TypesAnnotated.with(HTTPEndpoint.class)).asClass());
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getMethods()) {
                String endpointValue = null;
                String reqMethodTbl = switch (method) {
                    case Method m when m.isAnnotationPresent(GET.class) -> {
                        endpointValue = m.getAnnotation(GET.class).value();
                        yield "ReqGET";
                    }
                    case Method m when m.isAnnotationPresent(POST.class) -> {
                        endpointValue = m.getAnnotation(POST.class).value();
                        yield "ReqPOST";
                    }
                    case Method m when m.isAnnotationPresent(PUT.class) -> {
                        endpointValue = m.getAnnotation(PUT.class).value();
                        yield "ReqPUT";
                    }
                    case Method m when m.isAnnotationPresent(DELETE.class) -> {
                        endpointValue = m.getAnnotation(DELETE.class).value();
                        yield "ReqDELETE";
                    }
                    default -> null;
                };

                String statusAnnotationValue = null;
                String statusAnnotation = switch (method) {
                    case Method m when m.isAnnotationPresent(Gone.class) -> "GONE";
                    case Method m when m.isAnnotationPresent(MovedPermanently.class) -> {
                        statusAnnotationValue = m.getAnnotation(MovedPermanently.class).value();
                        yield "MOVED_PERMANENTLY";
                    }
                    case Method m when m.isAnnotationPresent(MovedTemporarily.class) -> {
                        statusAnnotationValue = m.getAnnotation(MovedTemporarily.class).value();
                        yield "FOUND";
                    }
                    case Method m when m.isAnnotationPresent(NotImplemented.class) -> {
                        statusAnnotationValue = m.getAnnotation(NotImplemented.class).value();
                        yield "NOT_IMPLEMENTED";
                    }
                    case Method m when m.isAnnotationPresent(PermanentRedirect.class) -> {
                        statusAnnotationValue = m.getAnnotation(PermanentRedirect.class).value();
                        yield "PERMANENT_REDIRECT";
                    }
                    case Method m when m.isAnnotationPresent(TemporaryRedirect.class) -> {
                        statusAnnotationValue = m.getAnnotation(TemporaryRedirect.class).value();
                        yield "TEMPORARY_REDIRECT";
                    }
                    case Method m when m.isAnnotationPresent(UnavailableForLegalReasons.class) -> {
                        statusAnnotationValue = m.getAnnotation(UnavailableForLegalReasons.class).value();
                        yield "UNAVAILABLE_FOR_LEGAL_REASONS";
                    }
                    default -> null;
                };

                if (reqMethodTbl == null) {
                    continue;
                }
                writeToDatabase(DATABASE, reqMethodTbl, endpointValue, clazz, method, statusAnnotation, statusAnnotationValue);
            }
        }
    }

    private static void writeToDatabase(final Connection DATABASE,
                                        final String TABLE,
                                        final String ENDPOINT,
                                        final Class<?> CLAZZ,
                                        final Method METHOD,
                                        final String STATUS_ANNOTATION,
                                        final String STATUS_ANNOTATION_VALUE) throws SQLException {
        String query = "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DATABASE.prepareStatement(query)) {
            ps.setString(1, (CLAZZ.getAnnotation(HTTPEndpoint.class).value().equals("/") ?
                    "" : CLAZZ.getAnnotation(HTTPEndpoint.class).value().toLowerCase(Locale.ROOT))
                    + ENDPOINT.toLowerCase(Locale.ROOT));
            ps.setString(2, CLAZZ.getName());
            ps.setString(3, METHOD.getName());
            ps.setString(4, STATUS_ANNOTATION);
            ps.setString(5, STATUS_ANNOTATION_VALUE);

            ps.executeUpdate();
        }
    }
}
