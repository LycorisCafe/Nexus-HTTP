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
import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.core.requestMethods.HTTPRequestMethod;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PUTProcessor implements MethodProcessor {
    private final RequestHandler REQ_HANDLER;
    private final BufferedInputStream INPUT_STREAM;
    private final Connection DATABASE;
    private final HTTPServerConfiguration CONFIG;

    public PUTProcessor(final RequestHandler REQ_HANDLER,
                        final BufferedInputStream INPUT_STREAM,
                        final Connection DATABASE,
                        final HTTPServerConfiguration CONFIG) {
        this.REQ_HANDLER = REQ_HANDLER;
        this.INPUT_STREAM = INPUT_STREAM;
        this.DATABASE = DATABASE;
        this.CONFIG = CONFIG;
    }

    @Override
    public HTTPResponse<?> process(HTTPRequest<?> request) {
        HTTPResponse<?> httpResponse = null;
        if (request.getHeaders().containsKey("content-length")) {
            int contentLen = Integer.parseInt(request.getHeaders().get("content-length").getFirst());
            if (contentLen > CONFIG.getMaxContentLength()) {
                REQ_HANDLER.processBadRequest(request.getREQUEST_ID(), HTTPStatusCode.BAD_REQUEST);
                return null;
            }

            byte[] bytes = new byte[contentLen];
            try {
                INPUT_STREAM.read(bytes, 0, bytes.length);
            } catch (IOException e) {
                REQ_HANDLER.processBadRequest(request.getREQUEST_ID(), HTTPStatusCode.INTERNAL_SERVER_ERROR);
                return null;
            }
            HTTPRequest<byte[]> httpReq = (HTTPRequest<byte[]>) request;
            httpReq.setContent(bytes);
        }
        try {
            List<String> details = Database.getEndpointDetails(DATABASE, HTTPRequestMethod.PUT, request.getRequestURL());
            if (details.get(0) == null) {
                REQ_HANDLER.processBadRequest(request.getREQUEST_ID(), HTTPStatusCode.NOT_FOUND);
                return httpResponse;
            }
            Class<?> clazz = Class.forName(details.get(1));
            Method method = clazz.getMethod(details.get(2), HTTPRequest.class);
            httpResponse = (HTTPResponse<?>) method.invoke(null, request);
        } catch (SQLException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException | IllegalAccessException e) {
            REQ_HANDLER.processBadRequest(request.getREQUEST_ID(), HTTPStatusCode.INTERNAL_SERVER_ERROR);
        }
        return httpResponse;
    }
}
