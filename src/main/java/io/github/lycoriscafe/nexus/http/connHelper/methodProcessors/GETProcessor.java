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

package io.github.lycoriscafe.nexus.http.connHelper.methodProcessors;

import io.github.lycoriscafe.nexus.http.connHelper.ConnectionHandler;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPResponse;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

public final class GETProcessor {
    private final ExecutorService EXECUTOR;
    private final ConnectionHandler CONN_HANDLER;
    private final int MAX_CONTENT_LENGTH;
    private final File TEMP_DIR;
    private final String[] APP_METHOD;
    private final HTTPRequest REQUEST;
    private HTTPResponse<?> response;

    public GETProcessor(final ExecutorService EXECUTOR,
                        final ConnectionHandler CONN_HANDLER,
                        final int MAX_CONTENT_LENGTH,
                        final File TEMP_DIR,
                        final String[] APP_METHOD,
                        final HTTPRequest REQUEST,
                        final HTTPResponse<?> RESPONSE) {
        this.EXECUTOR = EXECUTOR;
        this.CONN_HANDLER = CONN_HANDLER;
        this.MAX_CONTENT_LENGTH = MAX_CONTENT_LENGTH;
        this.TEMP_DIR = TEMP_DIR;
        this.APP_METHOD = APP_METHOD;
        this.REQUEST = REQUEST;
        response = RESPONSE;
    }

    public void process() {
        if (!REQUEST.getHeaders().get("CONTENT-LENGTH").isEmpty()) {
            // TODO handle content
        }

        if (EXECUTOR != null) {
            EXECUTOR.execute(this::processInPipe);
        } else {
            processInPipe();
        }
    }

    private void processInPipe() {
        try {
            Class<?> targetClass = Class.forName(APP_METHOD[0]);
            Method targetMethod = targetClass.getMethod(APP_METHOD[1], HTTPRequest.class, HTTPResponse.class);
            response = (HTTPResponse<?>) targetMethod.invoke(null, REQUEST, response);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            // TODO handle exceptions
        }

        CONN_HANDLER.addToSendQue(response);
    }
}
