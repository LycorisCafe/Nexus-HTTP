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
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.DELETE;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.PUT;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpDeleteRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpGetRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpPostRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpPutRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;

@HttpEndpoint
public class Endpoints {
    @GET("/")
    public static HttpResponse homePage(HttpGetRequest request,
                                        HttpResponse response) {
        return response.setContent(new Content("text/plain", "Home Page!"));
    }


    @GET("/sampleGetEndpoint")
    public static HttpResponse sampleGetEndpoint(HttpGetRequest request,
                                                 HttpResponse response) {
        return response.setContent(new Content("text/plain", "Sample Get Endpoint!"));
    }

    @POST("/samplePostEndpoint")
    public static HttpResponse samplePostEndpoint(HttpPostRequest request,
                                                  HttpResponse response) {
        return response.setContent(new Content("text/plain", "Sample Post Endpoint!"));
    }

    @PUT("/samplePutEndpoint")
    public static HttpResponse samplePutEndpoint(HttpPutRequest request,
                                                 HttpResponse response) {
        return response.setContent(new Content("text/plain", "Sample Put Endpoint!"));
    }

    @DELETE("/sampleDeleteEndpoint")
    public static HttpResponse sampleDeleteEndpoint(HttpDeleteRequest request,
                                                    HttpResponse response) {
        return response.setContent(new Content("text/plain", "Sample Delete Endpoint!"));
    }
}
