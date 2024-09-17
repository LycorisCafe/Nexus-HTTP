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

import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;

public final class GETProcessor implements MethodProcessor {
    private final ExecutorService EXECUTOR_SERVICE;
    private final RequestHandler REQ_HANDLER;
    private final BufferedInputStream INPUT_STREAM;
    private final Connection DATABASE;
    private final long MAX_CONTENT_LENGTH;
    private final File TEMP_DIR;

    public GETProcessor(final ExecutorService EXECUTOR_SERVICE,
                        final RequestHandler REQ_HANDLER,
                        final BufferedInputStream INPUT_STREAM,
                        final Connection DATABASE,
                        final long MAX_CONTENT_LENGTH,
                        final File TEMP_DIR) {
        this.EXECUTOR_SERVICE = EXECUTOR_SERVICE;
        this.REQ_HANDLER = REQ_HANDLER;
        this.INPUT_STREAM = INPUT_STREAM;
        this.DATABASE = DATABASE;
        this.MAX_CONTENT_LENGTH = MAX_CONTENT_LENGTH;
        this.TEMP_DIR = TEMP_DIR;
    }

    public HTTPResponse<?> process(final HTTPRequest<?> request) {
        return null;
    }
}
