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

package io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq;

import io.github.lycoriscafe.nexus.http.core.headers.auth.Authorization;

import java.io.File;
import java.util.List;

public final class HttpPostRequest extends HttpRequest {
    private Authorization authorization;
    private List<HttpPostContent> httpPostContents;

    public HttpPostRequest(final long requestId,
                           final String endpoint) {
        super(requestId, endpoint);
    }

    public void setAuthorization(final Authorization authorization) {
        if (authorization == null) {
            throw new IllegalArgumentException("Authorization cannot be null");
        }
        this.authorization = authorization;
    }

    public void setContent(final String name,
                           final String fileName,
                           final Object content)
            throws IllegalArgumentException {
        if (!(content instanceof byte[] || content instanceof File)) {
            throw new IllegalArgumentException("Content must be a byte array or a file. " +
                    "If you need this to be null, just ignore this method.");
        }
        httpPostContents.add(new HttpPostContent(name, fileName, content));
    }

    public List<HttpPostContent> getHttpPostContents() {
        return httpPostContents;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    // TODO rethink about post data management
    public record HttpPostContent(String name,
                                  String fileName,
                                  Object content) {
    }
}
