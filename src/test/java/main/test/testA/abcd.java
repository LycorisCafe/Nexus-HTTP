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

package main.test.testA;

import io.github.lycoriscafe.nexus.http.core.HTTPEndpoint;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HTTPEndpoint("/")
public class abcd {
    @GET("/abcd")
    public static HTTPResponse<?> xyz(HTTPRequest<?> request) {
        System.out.println(request.getParameters());
        HTTPResponse<String> httpResponse = new HTTPResponse<>(request.getREQUEST_ID());
        httpResponse.setStatusCode(HTTPStatusCode.OK);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", List.of("text/html"));
        httpResponse.setHeaders(headers);
        httpResponse.setContent("Hello World!");
        return httpResponse;
    }

    @POST("/efgh")
    public static HTTPResponse<?> abc(HTTPRequest<?> request) {
        System.out.println(request.getParameters());
        HTTPResponse<String> httpResponse = new HTTPResponse<>(request.getREQUEST_ID());
        httpResponse.setStatusCode(HTTPStatusCode.OK);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", List.of("text/html"));
        httpResponse.setHeaders(headers);
        httpResponse.setContent("Hello World!");
        return httpResponse;
    }
}
