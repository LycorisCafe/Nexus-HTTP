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

import io.github.lycoriscafe.nexus.http.core.HTTPVersion;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;

import java.util.List;
import java.util.Map;

public final class HTTPResponse<T> {
    private final long RESPONSE_ID;
    private HTTPVersion version;
    private HTTPStatusCode statusCode;
    private Map<String, List<String>> headers;
    private T content;

    public HTTPResponse(final long RESPONSE_ID) {
        this.RESPONSE_ID = RESPONSE_ID;
    }

    public long getRESPONSE_ID() {
        return RESPONSE_ID;
    }

    public HTTPVersion getVersion() {
        return version;
    }

    public void setVersion(HTTPVersion version) {
        this.version = version;
    }

    public HTTPStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HTTPStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
