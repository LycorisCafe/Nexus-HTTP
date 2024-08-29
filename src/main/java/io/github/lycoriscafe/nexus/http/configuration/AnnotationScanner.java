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

import com.google.gson.Gson;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.HTTPEndpoint;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.DELETE;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.PUT;
import io.github.lycoriscafe.nexus.http.httpHelper.valueAnnotations.Header;
import io.github.lycoriscafe.nexus.http.httpHelper.valueAnnotations.Param;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public class AnnotationScanner {
    public static void scan(final Connection DATABASE,
                            final String BASE_PACKAGE) throws SQLException {
        Reflections reflections = new Reflections(BASE_PACKAGE);
        Set<Class<?>> modules = reflections.get(SubTypes.of(TypesAnnotated.with(HTTPEndpoint.class)).asClass());
        for (Class<?> module : modules) {
            for (Method method : module.getMethods()) {
                if (method.isAnnotationPresent(GET.class)) {
                    ArrayList<String> headers = new ArrayList<>();
                    ArrayList<String> params = new ArrayList<>();
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        if (parameter.isAnnotationPresent(Header.class)) {
                            headers.add(parameter.getName().toLowerCase(Locale.ROOT));
                        }
                        if (parameter.isAnnotationPresent(Param.class)) {
                            params.add(parameter.getName().toLowerCase(Locale.ROOT));
                        }
                    }

                    writeGetToDatabase(DATABASE, "ReqGET",
                            module.getAnnotation(HTTPEndpoint.class).value()
                                    + method.getAnnotation(GET.class).value(),
                            module.getPackageName() + "." + module.getName(),
                            headers, params);
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

    private static void writeGetToDatabase(final Connection DATABASE,
                                           final String ENDPOINT,
                                           final String CLASS_NAME,
                                           final String METHOD_NAME,
                                           final ArrayList<String> REQ_HEADERS,
                                           final ArrayList<String> REQ_VALUES) throws SQLException {
        String query = "INSERT INTO ReqGET VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DATABASE.prepareStatement(query)) {
            ps.setString(1, ENDPOINT);
            ps.setString(2, CLASS_NAME);
            ps.setString(3, METHOD_NAME);

            Gson gson = new Gson();
            String headersJson = REQ_HEADERS.isEmpty() ? null : gson.toJson(REQ_HEADERS);
            ps.setString(4, headersJson);

            String reqValuesJson = REQ_VALUES.isEmpty() ? null : gson.toJson(REQ_VALUES);
            ps.setString(5, reqValuesJson);

            ps.executeUpdate();
        }
    }
}
