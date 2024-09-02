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

import io.github.lycoriscafe.nexus.http.httpHelper.meta.HTTPVersion;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.HTTPRequestMethod;

import java.util.List;
import java.util.Map;

public final class HTTPRequest {
    private final long REQUEST_ID;
    private final HTTPRequestMethod METHOD;
    private final Map<String, String> PARAMETERS;
    private final HTTPVersion VERSION;
    private final Map<String, List<String>> HEADERS;

    public HTTPRequest(final long REQUEST_ID,
                       final HTTPRequestMethod METHOD,
                       final Map<String, String> PARAMETERS,
                       final HTTPVersion VERSION,
                       final Map<String, List<String>> HEADERS) {
        this.REQUEST_ID = REQUEST_ID;
        this.METHOD = METHOD;
        this.PARAMETERS = PARAMETERS;
        this.VERSION = VERSION;
        this.HEADERS = HEADERS;
    }

    public long getRequestID() {
        return REQUEST_ID;
    }

    public HTTPRequestMethod getMethod() {
        return METHOD;
    }

    public Map<String, String> getParameters() {
        return PARAMETERS;
    }

    public HTTPVersion getVersion() {
        return VERSION;
    }

    public Map<String, List<String>> getHeaders() {
        return HEADERS;
    }
}
