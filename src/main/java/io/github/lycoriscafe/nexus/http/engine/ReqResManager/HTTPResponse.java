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

package io.github.lycoriscafe.nexus.http.engine.ReqResManager;

import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HTTPResponse<T> {
    private final long RESPONSE_ID;

    private HTTPStatusCode HTTPStatusCode;
    private Map<String, List<String>> headers;
    private Map<String, ArrayList<String>> customHeaders;
    private String contentType;
    private int contentLength;
    private T content;

    public HTTPResponse(final long RESPONSE_ID) {
        this.RESPONSE_ID = RESPONSE_ID;
    }

    public HTTPResponse<?> setStatusCode(final HTTPStatusCode HTTPStatusCode) {
        this.HTTPStatusCode = HTTPStatusCode;
        return this;
    }

    public HTTPResponse<?> setHeaders(final Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public HTTPResponse<?> setCustomHeaders(final Map<String, ArrayList<String>> customHeaders) {
        this.customHeaders.putAll(customHeaders);
        return this;
    }

    public HTTPResponse<?> setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HTTPResponse<T> setContent(final T content) {
        this.content = content;
        return this;
    }

    public long getResponseId() {
        return RESPONSE_ID;
    }

    public Map<String, ArrayList<String>> getCustomHeaders() {
        return customHeaders;
    }
}
