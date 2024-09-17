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

package io.github.lycoriscafe.nexus.http.engine.methodProcessor;

import io.github.lycoriscafe.nexus.http.configuration.Database;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HTTPRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestHandler;

import java.io.BufferedInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class GETProcessor implements MethodProcessor {
    private final RequestHandler REQ_HANDLER;
    private final BufferedInputStream INPUT_STREAM;
    private final Connection DATABASE;

    public GETProcessor(final RequestHandler REQ_HANDLER,
                        final BufferedInputStream INPUT_STREAM,
                        final Connection DATABASE) {
        this.REQ_HANDLER = REQ_HANDLER;
        this.INPUT_STREAM = INPUT_STREAM;
        this.DATABASE = DATABASE;
    }

    public HTTPResponse<?> process(final HTTPRequest<?> request) {
        HTTPResponse<?> httpResponse = null;
        try {
            List<String> details = Database.getEndpointDetails(DATABASE, HTTPRequestMethod.GET, request.getRequestURL());
            Class<?> clazz = details.get(1).getClass();
            Method method = clazz.getMethod(details.get(2), HTTPRequest.class);
            httpResponse = (HTTPResponse<?>) method.invoke(null, request);
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            REQ_HANDLER.processBadRequest(request.getREQUEST_ID(), HTTPStatusCode.INTERNAL_SERVER_ERROR);
        }
        return httpResponse;
    }
}
