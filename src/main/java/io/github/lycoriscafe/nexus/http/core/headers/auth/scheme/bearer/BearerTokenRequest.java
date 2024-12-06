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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpPostRequest;

import java.util.Map;

public final class BearerTokenRequest {
    private final String grantType;
    private Map<String, String> params;

    public BearerTokenRequest(final String grantType) {
        this.grantType = grantType;
    }

    public String getGrantType() {
        return grantType;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public BearerTokenRequest setParams(final Map<String, String> params) {
        this.params = params;
        return this;
    }

    @SuppressWarnings("unchecked")
    public static BearerTokenRequest parse(final HttpPostRequest request) {
        Map<String, String> params;
        if (!(request.getContent().getData() instanceof Map)) {
            return null;
        }
        params = (Map<String, String>) request.getContent().getData();

        if (!params.containsKey("grant_type")) {
            return null;
        }

        BearerTokenRequest bearerTokenRequest = new BearerTokenRequest(params.get("grant_type"));
        params.remove("grant_type");
        return bearerTokenRequest.setParams(params);
    }
}
