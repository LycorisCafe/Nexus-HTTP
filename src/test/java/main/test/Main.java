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
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authenticated;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.basic.BasicAuthentication;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerTokenRequest;
import io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer.BearerTokenResponse;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.GET;
import io.github.lycoriscafe.nexus.http.core.requestMethods.annotations.POST;
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
                .setPort(2004)
                .setStaticFilesDirectory(null)
                .setDatabaseLocation("")
                .addDefaultAuthentication(new BasicAuthentication("HelloWorld"));
        HttpServer httpServer = new HttpServer(httpServerConfiguration);
        httpServer.initialize();
    }

    @GET("/")
    public static HttpResponse helloEndpoint(final HttpGetRequest httpGetRequest,
                                             final HttpResponse httpResponse) {
        return httpResponse.setContent(new Content("text/plan", "Hello world"))
                .setCookie(new Cookie("testCookie", "testCookieValue"));
    }

    @GET("/test")
    @Authenticated
    public static HttpResponse authTestEndpoint(final HttpGetRequest httpGetRequest,
                                                final HttpResponse httpResponse) {
        return httpResponse.setContent(new Content("text/plan", "Test Endpoint!").setContentEncodingGzipped(true));
    }

    @GET("/img")
    @Authenticated
    public static HttpResponse imgEndpoint(final HttpGetRequest httpGetRequest,
                                           final HttpResponse httpResponse) {
        return httpResponse.setContent(new Content("video/mp4", Paths.get("D:\\Media\\c97123a510d037aabd51db740738cf1a.mp4")));
    }

    @BearerEndpoint(@POST("/generateToken"))
    public static BearerTokenResponse tokenEndpoint(final BearerTokenRequest bearerTokenRequest,
                                                    final BearerTokenResponse bearerTokenResponse) {
        return bearerTokenResponse.setBearerToken("abcd");
    }
}
