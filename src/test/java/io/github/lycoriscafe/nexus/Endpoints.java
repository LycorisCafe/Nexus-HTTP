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

package io.github.lycoriscafe.nexus;

import io.github.lycoriscafe.nexus.http.core.HttpEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpGetRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;

@HttpEndpoint
public class Endpoints {
    @GET("/")
    public static HttpResponse homePage(HttpGetRequest request,
                                        HttpResponse response) {
        // Will redirect to "/helloWorld" endpoint
        return response.setStatusCode(HttpStatusCode.TEMPORARY_REDIRECT).addHeader(new Header("Location", "/helloWorld"));
    }

    @GET("/helloWorld")
    public static HttpResponse helloWorld(HttpGetRequest request,
                                          HttpResponse response) {
        // Will return "Hello World!"
        return response.setContent(new Content("text/plain", "Hello World!"));
    }

    @GET("/hello")
    public static HttpResponse helloPerson(HttpGetRequest request,
                                           HttpResponse response) {
        // Will return "Hello {name}!". {name} is an url parameter. If null, "Hello John!".
        String name = request.getParameters() == null ? "John" : request.getParameters().get("name");
        return response.setContent(new Content("text/plain", "Hello " + name + "!"));
    }
}
