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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class HTTPResponse<T> {
    private final long RESPONSE_ID;
    private HTTPVersion version;
    private HTTPStatusCode statusCode;
    private Map<String, List<String>> headers;
    private T content;
    StringBuilder protocolBody;

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
        this.headers.putAll(headers);
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public void formatProtocol() {
        protocolBody = new StringBuilder(version.getValue() + " " + statusCode.getStatusCode() + "\r\n");
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            protocolBody.append(header.getKey()).append(": ");
            for (int i = 0; i < header.getValue().size(); ++i) {
                protocolBody.append(header.getValue().get(i));
                if (i != header.getValue().size()) {
                    protocolBody.append(", ");
                }
            }
            protocolBody.append("\r\n");
        }

        if (content != null) {
            protocolBody.append("Content-Length: ");
            if (content instanceof byte[] b) {
                protocolBody.append(b.length);
            }
            if (content instanceof String str) {
                protocolBody.append(str.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
            }
            if (content instanceof File file) {
                protocolBody.append(file.getTotalSpace()).append("\r\n");
            }
        }
        protocolBody.append("\r\n");
    }

    public byte[] getFormattedProtocol() {
        return protocolBody.toString().getBytes(StandardCharsets.UTF_8);
    }
}
