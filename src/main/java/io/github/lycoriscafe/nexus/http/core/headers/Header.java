/*
 * Copyright 2024 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.core.headers;

import java.util.List;
import java.util.Objects;

/**
 * General HTTP header for requests/responses. This is a primary level type that converts provided name and value directly to an HTTP header and so.
 * <pre>
 *     {@code
 *     // Example code
 *     var header = new Header("My-Header", "myValue=value");
 *     }
 *     {@code
 *     <!-- Example code -->
 *     My-Header: myValue=value
 *     }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9110">HTTP Semantics (rfc 9110)</a>
 * @since v1.0.0
 */
public final class Header {
    private final String name;
    private final String value;

    /**
     * Create an instance of {@code Header}.
     *
     * @param name  Header name
     * @param value Header value
     * @see Header
     * @since v1.0.0
     */
    public Header(final String name,
                  final String value) {
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Get provided header name
     *
     * @return Header name
     * @see Header
     * @since v1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Get provided header value
     *
     * @return Header value
     * @see Header
     * @since v1.0.0
     */
    public String getValue() {
        return value;
    }

    /**
     * Process incoming non-specific header (not defined in server) in to primary {@code Header}.
     *
     * @param headerParts Key value pair
     * @return New {@code Header} instance
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Header
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
     * @since v1.0.0
     */
    public static Header parseIncomingHeader(final String[] headerParts) {
        return new Header(headerParts[0], headerParts[1].trim());
    }

    /**
     * Process outgoing {@code Header}s to HTTP header strings.
     *
     * @param headers {@code List} fo {@code Header} instances
     * @return HTTP headers string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see Header
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
     * @since v1.0.0
     */
    public static String parseOutgoingHeaders(final List<Header> headers) {
        if (headers == null || headers.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        for (Header header : headers) {
            output.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
        }
        return output.toString();
    }
}
