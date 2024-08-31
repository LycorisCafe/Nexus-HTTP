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

import io.github.lycoriscafe.nexus.http.httpHelper.meta.HTTPEndpoint;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.DELETE;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.PUT;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public final class AnnotationScanner {
    public static void scan(final Connection DATABASE,
                            final String BASE_PACKAGE) throws SQLException {
        Reflections reflections = new Reflections(BASE_PACKAGE);
        Set<Class<?>> modules = reflections.get(SubTypes.of(TypesAnnotated.with(HTTPEndpoint.class)).asClass());
        for (Class<?> module : modules) {
            for (Method method : module.getMethods()) {
                if (method.isAnnotationPresent(GET.class)) {
                    writeToDatabase(
                            DATABASE,
                            "ReqGET",
                            (module.getAnnotation(HTTPEndpoint.class).value().equals("/") ?
                                    "" : module.getAnnotation(HTTPEndpoint.class).value())
                                    + method.getAnnotation(GET.class).value(),
                            module.getName(),
                            method.getName()
                    );
                }
                if (method.isAnnotationPresent(POST.class)) {

                }
                if (method.isAnnotationPresent(PUT.class)) {

                }
                if (method.isAnnotationPresent(DELETE.class)) {

                }
            }
        }
    }

    private static void writeToDatabase(final Connection DATABASE,
                                        final String TABLE,
                                        final String ENDPOINT,
                                        final String CLASS_NAME,
                                        final String METHOD_NAME) throws SQLException {
        String query = "INSERT INTO " + TABLE + " VALUES (?, ?, ?)";
        try (PreparedStatement ps = DATABASE.prepareStatement(query)) {
            ps.setString(1, ENDPOINT);
            ps.setString(2, CLASS_NAME);
            ps.setString(3, METHOD_NAME);

            ps.executeUpdate();
        }
    }
}
