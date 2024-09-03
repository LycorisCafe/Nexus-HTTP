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
import io.github.lycoriscafe.nexus.http.core.requestMethods.HTTPRequestMethod;

import java.util.List;
import java.util.Map;

public final class HTTPRequest<T> {
    private final long REQUEST_ID;
    private HTTPRequestMethod requestMethod;
    private String requestURL;
    private Map<String, String> parameters;
    private HTTPVersion version;
    private Map<String, List<String>> headers;
    private T content;

    public HTTPRequest(final long REQUEST_ID) {
        this.REQUEST_ID = REQUEST_ID;
    }

    public long getREQUEST_ID() {
        return REQUEST_ID;
    }

    public HTTPRequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(HTTPRequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public HTTPVersion getVersion() {
        return version;
    }

    public void setVersion(HTTPVersion version) {
        this.version = version;
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
