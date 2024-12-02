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

package main.test;

import io.github.lycoriscafe.nexus.http.HttpServer;
import io.github.lycoriscafe.nexus.http.HttpServerException;
import io.github.lycoriscafe.nexus.http.core.HttpEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpGetRequest;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.scanners.ScannerException;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

@HttpEndpoint
public class Main {
    public static void main(String[] args) throws ScannerException, SQLException, IOException, HttpServerException {
        HttpServerConfiguration httpServerConfiguration = new HttpServerConfiguration("main.test")
                .port(2004)
                .staticFilesDirectory(null);
        HttpServer httpServer = new HttpServer(httpServerConfiguration);
        httpServer.initialize();
    }

    @GET("/")
    public static HttpResponse helloEndpoint(final HttpGetRequest httpGetRequest) {
        System.out.println("Method called!");
        return new HttpResponse(httpGetRequest.getRequestId(),
                httpGetRequest.getRequestConsumer(),
                HttpStatusCode.OK)
                .content(new Content("image/jpg", Paths.get("D:\\Media\\45e9989c6cc9b5d0db8f1fe67d07c177.jpg")));
    }
}
