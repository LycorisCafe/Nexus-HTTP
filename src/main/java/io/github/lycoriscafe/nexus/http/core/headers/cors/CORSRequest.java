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

package io.github.lycoriscafe.nexus.http.core.headers.cors;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.util.List;
import java.util.Locale;

public final class CORSRequest {
    private String origin;
    private HttpRequestMethod accessControlRequestMethod;
    private List<String> accessControlRequestHeaders;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    public HttpRequestMethod getAccessControlRequestMethod() {
        return accessControlRequestMethod;
    }

    public void setAccessControlRequestMethod(final HttpRequestMethod accessControlRequestMethod) {
        this.accessControlRequestMethod = accessControlRequestMethod;
    }

    public List<String> getAccessControlRequestHeaders() {
        return accessControlRequestHeaders;
    }

    public void setAccessControlRequestHeader(final String accessControlRequestHeader) {
        if (accessControlRequestHeaders == null) accessControlRequestHeaders = new NonDuplicateList<>();
        accessControlRequestHeaders.add(accessControlRequestHeader);
    }

    public static CORSRequest processIncomingCors(CORSRequest request,
                                                  final String[] values) {
        if (request == null) request = new CORSRequest();
        switch (values[0].toLowerCase(Locale.US)) {
            case "origin" -> request.setOrigin(values[1].trim());
            case "access-control-request-method" -> request.setAccessControlRequestMethod(HttpRequestMethod.validate(values[1].trim()));
            case "access-control-request-headers" -> {
                String[] headers = values[1].split(",", 0);
                for (String header : headers) {
                    request.setAccessControlRequestHeader(header.trim());
                }
            }
        }
        return request;
    }
}
