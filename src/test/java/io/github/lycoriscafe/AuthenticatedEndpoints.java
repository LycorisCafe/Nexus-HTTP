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

package io.github.lycoriscafe;

import io.github.lycoriscafe.nexus.http.core.HttpEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.auth.AuthScheme;
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authenticated;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.basic.BasicAuthorization;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpGetRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;

@HttpEndpoint
public class AuthenticatedEndpoints {
    @Authenticated
    @GET("/sampleAuthenticatedEndpoint")
    public static HttpResponse sampleAuthenticatedEndpoint(HttpGetRequest request,
                                                           HttpResponse response) {
        // Either these methods will work
        // if (request.getAuthorization() instanceof BasicAuthorization) {}
        if (request.getAuthorization().getAuthScheme() == AuthScheme.BASIC) {
            var authorization = (BasicAuthorization) request.getAuthorization();
            if (authorization.getUsername().equals("root") && authorization.getPassword().equals("root")) {
                return response.setContent(new Content("text/plain", "Authenticated!"));
            }
        }
        // ...
        return response.setContent(new Content("text/plain", "Unauthenticated!"));
    }
}
