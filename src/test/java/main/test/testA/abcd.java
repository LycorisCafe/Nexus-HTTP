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
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.DELETE;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.PUT;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HTTPStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.HTTPResponse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HTTPEndpoint("/")
public class abcd {
    @GET("/abcd")
    public static HTTPResponse<?> xyz(HTTPRequest<?> request) {
        System.out.println(request.getParameters());
        HTTPResponse<File> httpResponse = new HTTPResponse<>(request.getREQUEST_ID());
        httpResponse.setStatusCode(HTTPStatusCode.OK);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", List.of("image/jpg"));
        httpResponse.setHeaders(headers);
        httpResponse.setContent(new File("D:\\Media\\45e9989c6cc9b5d0db8f1fe67d07c177.jpg"));
        return httpResponse;
    }

    static int x = 0;

    @POST("/sensor-data")
    public static HTTPResponse<?> abc(HTTPRequest<?> request) {
//        System.out.println(request.getParameters());
//        System.out.println(request.getHeaders());
        byte[] b = (byte[]) request.getContent();
        for (byte x : b) {
            System.out.print((char) x);
        }
        System.out.println();
        HTTPResponse<String> httpResponse = new HTTPResponse<>(request.getREQUEST_ID());
        httpResponse.setStatusCode(HTTPStatusCode.OK);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", List.of("text/html"));
        httpResponse.setHeaders(headers);
        if (x == 0) {
            x = 1;
        } else {
            x = 0;
        }
        httpResponse.setContent(x == 1 ? "relay_on" : "relay_off");
        return httpResponse;
    }

    @PUT("/ijkl")
    public static HTTPResponse<?> defg(HTTPRequest<?> request) {
        System.out.println(request.getParameters());
        HTTPResponse<String> httpResponse = new HTTPResponse<>(request.getREQUEST_ID());
        httpResponse.setStatusCode(HTTPStatusCode.CREATED);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", List.of("text/html"));
        httpResponse.setHeaders(headers);
        httpResponse.setContent("Hello World!");
        return httpResponse;
    }

    @DELETE("/mnop")
    public static HTTPResponse<?> xyza(HTTPRequest<?> request) {
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
