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

import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.RequestMethod;

import java.util.List;
import java.util.Map;

public final class HTTPRequest {
    private final int REQUEST_ID;
    private final RequestMethod METHOD;
    private final Map<String, List<String>> HEADERS;

    public HTTPRequest(final int REQUEST_ID,
                       final RequestMethod METHOD,
                       final Map<String, List<String>> HEADERS) {
        this.REQUEST_ID = REQUEST_ID;
        this.METHOD = METHOD;
        this.HEADERS = HEADERS;
    }

    public int getRequestID() {
        return REQUEST_ID;
    }

    public RequestMethod getMethod() {
        return METHOD;
    }

    public Map<String, List<String>> getHeaders() {
        return HEADERS;
    }
}