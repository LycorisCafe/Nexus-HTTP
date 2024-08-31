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

package io.github.lycoriscafe.nexus.http.httpHelper.manager;

import io.github.lycoriscafe.nexus.http.httpHelper.meta.statusCodes.StatusCode;

import java.util.ArrayList;
import java.util.Map;

public final class HTTPResponse {
    private final int RESPONSE_ID;

    private String pre_headers;

    private StatusCode statusCode;
    private Map<String, ArrayList<String>> headers;
    private int contentLength;
    private byte[] body;

    public HTTPResponse(final int RESPONSE_ID) {
        this.RESPONSE_ID = RESPONSE_ID;
    }

    public HTTPResponse setHeaders(final Map<String, ArrayList<String>> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public Map<String, ArrayList<String>> getHeaders() {
        return headers;
    }
}
