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

package io.github.lycoriscafe.nexus.http.helper.configuration;

import io.github.lycoriscafe.nexus.http.HttpServer;
import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication;
import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.headers.cors.CORSResponse;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ReportingEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

/**
 * Configurations configuration for {@code HttpServer}.
 *
 * @see HttpServer
 * @since v1.0.0
 */
public final class HttpServerConfiguration {
    private int port = 0;
    private int backlog = 0;
    private InetAddress inetAddress = null;
    private int connectionTimeout = 60_000;
    private ThreadType threadType = ThreadType.VIRTUAL;

    private final String basePackage;
    private String urlExtension = "";
    private String tempDirectory = "NexusTemp";
    private String staticFilesDirectory = null;
    private DatabaseType databaseType = DatabaseType.TEMPORARY;

    private int maxHeaderSize = 10_240;
    private int maxHeadersPerRequest = 20;
    private int maxIncomingConnections = 100;
    private int maxContentLength = 5_242_880;
    private int maxChunkedContentLength = 104_857_600;
    private int maxChunkSize = 5_242_880;

    private List<Header> defaultHeaders = null;
    private List<Authentication> defaultAuthentications = null;
    private List<Cookie> defaultCookies = null;
    private CORSResponse defaultCcorsResponse = null;
    private List<ReportingEndpoint> reportingEndpoints = null;
    private List<ContentSecurityPolicy> defaultContentSecurityPolicies = null;
    private List<ContentSecurityPolicyReportOnly> defaultContentSecurityPolicyReportOnly = null;
    private StrictTransportSecurity defaultStrictTransportSecurity = null;
    private CacheControl defaultCacheControl = null;
    private boolean defaultXContentTypeOptionsNoSniff;

    private boolean addErrorMessageToResponseHeaders = true;

    /**
     * Create an instance of {@code HttpServerConfiguration}.
     *
     * @param basePackage Package that need to scan for {@code HttpEndpoint} classes. Sub packages will be also included.
     * @apiNote <pre>
     * {@code
     * // Example endpoint scan scenario
     * var serverConfig = new HttpServerConfiguration("org.example.server_x");
     * _
     * |- org.example
     *      |- server_x
     *          |- Class_1.java [included]
     *          |- Class_2.java [included]
     *          |- sub_package_1
     *              |- Class_3.java [included]
     *      |- server_y
     *          |- Class_4.java [not included]
     *          |- Class_5.java [not included]
     *          |- sub_package_2
     *              |- Class_6.java [not included]
     * }
     * </pre>
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration(final String basePackage) {
        this.basePackage = Objects.requireNonNull(basePackage);
    }

    /**
     * Get provided base package.
     *
     * @return Base package
     * @see #HttpServerConfiguration(String)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Set server port number.If you didn't specify it here, after initialize the server, default value will be updated to the actual port number.
     *
     * @param port Server port number. Default is 0 (it means an implementation specific default will be used).
     * @return Same {@code HttpServerConfiguration} instance
     * @see ServerSocket#ServerSocket(int, int)
     * @see ServerSocket#ServerSocket(int, int, InetAddress)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setPort(final int port) {
        this.port = port;
        return this;
    }

    /**
     * Get server port number. If you didn't specify the port number, a random system dependent port number will be chosen. It will print when server
     * initialization.
     *
     * @return Server port number
     * @see #setPort(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getPort() {
        return port;
    }

    /**
     * Set server backlog value.
     *
     * @param backlog Server backlog. Default is 0 (it means an implementation specific default will be used).
     * @return Same {@code HttpServerConfiguration} instance
     * @see ServerSocket#ServerSocket(int, int)
     * @see ServerSocket#ServerSocket(int, int, InetAddress)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setBacklog(final int backlog) {
        this.backlog = backlog;
        return this;
    }

    /**
     * Get server backlog value.
     *
     * @return Server backlog
     * @see #setBacklog(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getBacklog() {
        return backlog;
    }

    /**
     * Set InetAddress value. Default is {@code null}.
     *
     * @param inetAddress InetAddress
     * @return Same {@code HttpServerConfiguration} instance
     * @see InetAddress
     * @see ServerSocket#ServerSocket(int, int)
     * @see ServerSocket#ServerSocket(int, int, InetAddress)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setInetAddress(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }

    /**
     * Get InetAddress value.
     *
     * @return InetAddress
     * @see #setInetAddress(InetAddress)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Set connection timeout value for each incoming connection. Default is {@code 60,000 milliseconds} (1 minute).
     *
     * @param connectionTimeout Timeout value in milliseconds
     * @return Same {@code HttpServerConfiguration} instance
     * @see Socket#setSoTimeout(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * Get connection timeout value for each incoming connection.
     *
     * @return Timeout value
     * @see #setConnectionTimeout(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set thread type for incoming requests. Default is {@code ThreadType.VIRTUAL}.
     *
     * @param threadType Thread type
     * @return Same {@code HttpServerConfiguration} instance
     * @see ThreadType
     * @see Thread
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setThreadType(final ThreadType threadType) {
        this.threadType = Objects.requireNonNull(threadType);
        return this;
    }

    /**
     * Get thread type for incoming requests.
     *
     * @return Thread type
     * @see #setThreadType(ThreadType)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public ThreadType getThreadType() {
        return threadType;
    }

    /**
     * Set temporary directory to process in-API tasks such as content handling for requests/responses. Default is {@code "NexusTemp"} in same
     * directory. This directory cannot be null and must have read and write permissions.
     *
     * @param tempDirectory Temporary directory for in-API tasks
     * @return Same {@code HttpServerConfiguration} instance
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setTempDirectory(final String tempDirectory) {
        Objects.requireNonNull(tempDirectory);
        if (tempDirectory.isBlank()) throw new IllegalStateException("temp directory cannot be empty/blank");
        this.tempDirectory = tempDirectory;
        return this;
    }

    /**
     * Get temporary directory.
     *
     * @return Temporary directory
     * @see #setTempDirectory(String)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public String getTempDirectory() {
        return tempDirectory;
    }

    /**
     * Set prefix to the endpoint. Default is empty.
     * <pre>
     *     {@code
     *     // This code will give -> http://host:port/api/v1.0.0/sampleEndpointClass/sampleEndpoint
     *
     *     var serverConfig = new HttpServerConfiguration("org.example")
     *          .setUrlPrefix("/api/v1.0.0");
     *
     *     // ...
     *
     *     @HttpEndpoint("/sampleEndpointClass")
     *     public class SampleEndpointClass {
     *          @GET("/sampleEndpoint")
     *          public static HttpResponse sampleEndpoint(HttpRequest request,
     *                                                    HttpResponse response) {
     *              // ...
     *              return response;
     *          }
     *     }
     *     }
     * </pre>
     *
     * @param urlPrefix URL prefix
     * @return Same {@code HttpServerConfiguration} instance
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setUrlPrefix(final String urlPrefix) {
        Objects.requireNonNull(urlPrefix);
        this.urlExtension = urlPrefix.startsWith("\\") ? urlPrefix : "\\" + urlPrefix;
        return this;
    }

    /**
     * Get URL prefix.
     *
     * @return URL prefix
     * @see #setUrlPrefix(String)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public String getUrlPrefix() {
        return urlExtension;
    }

    /**
     * Set static files' directory. This directory will serve static contents like {@code .html}. Default is {@code null}.
     * <pre>
     *     {@code
     *     var serverConfig = new HttpServerConfiguration("org.example")
     *          .setStaticFilesDirectory("NexusStaticFiles");
     *
     *      NexusStaticFiles
     *      |- TestFile.html [http://host:port/TestFile.html]
     *      |- styles
     *          |- Styles.css [http://host:port/styles/Styles.css]
     *     }
     * </pre>
     *
     * @param staticFilesDirectory Static files directory
     * @return Same {@code HttpServerConfiguration} instance
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setStaticFilesDirectory(final String staticFilesDirectory) {
        this.staticFilesDirectory = staticFilesDirectory;
        return this;
    }

    /**
     * Get static files' directory.
     *
     * @return Static files directory
     * @see #setStaticFilesDirectory(String)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public String getStaticFilesDirectory() {
        return staticFilesDirectory;
    }

    /**
     * Set database creation type. Default is {@code TEMPORARY}.
     *
     * @param databaseType Database type
     * @return Same {@code HttpServerConfiguration} instance
     * @see DatabaseType
     * @see Database
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDatabaseType(final DatabaseType databaseType) {
        this.databaseType = Objects.requireNonNull(databaseType);
        return this;
    }

    /**
     * Get database type.
     *
     * @return Database type
     * @see DatabaseType
     * @see #setDatabaseType(DatabaseType)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * Set maximum header size in bytes. Default is {@code 10,240}.
     *
     * @param maxHeaderSize Maximum header size in bytes
     * @return Same {@code HttpServerConfiguration} instance
     * @apiNote {@code 1 byte = 1 character}. The actual size calculated by {@code maxHeaderSize - 2}.
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setMaxHeaderSize(final int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
        return this;
    }

    /**
     * Get maximum header size value.
     *
     * @return Maximum header size
     * @see #setMaxHeaderSize(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    /**
     * Set maximum headers count per request. Default is {@code 20}.
     *
     * @param maxHeadersPerRequest Maximum headers count per request
     * @return Same {@code HttpServerConfiguration} instance
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setMaxHeadersPerRequest(final int maxHeadersPerRequest) {
        if (maxHeadersPerRequest < 2) {
            throw new IllegalStateException("max headers per request cannot be less than 2");
        }
        this.maxHeadersPerRequest = maxHeadersPerRequest;
        return this;
    }

    /**
     * Get maximum header count per request.
     *
     * @return Maximum headers count per request
     * @see #setMaxHeadersPerRequest(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getMaxHeadersPerRequest() {
        return maxHeadersPerRequest;
    }

    /**
     * Set maximum incoming connections for server. If this count is overwhelm, next connections will go to backlog until remaining connection dies.
     * Default is {@code 100}.
     *
     * @param maxIncomingConnections Maximum incoming connections for server
     * @return Same {@code HttpServerConfiguration} instance
     * @see #setBacklog(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setMaxIncomingConnections(final int maxIncomingConnections) {
        if (maxIncomingConnections < 1) throw new IllegalStateException("max incoming connection count cannot be less than 1");
        this.maxIncomingConnections = maxIncomingConnections;
        return this;
    }

    /**
     * Get maximum incoming connections for server.
     *
     * @return Maximum incoming connections for server
     * @see #setMaxIncomingConnections(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getMaxIncomingConnections() {
        return maxIncomingConnections;
    }

    /**
     * Set maximum content length that can process with an incoming request in {@code bytes}. Default is {@code 5,242,880}.
     *
     * @param maxContentLength Maximum content length
     * @return Same {@code HttpServerConfiguration} instance
     * @apiNote This will not apply to the requests that comes with {@code Transfer-Encoding: chunked} header.
     * @see #setMaxChunkedContentLength(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setMaxContentLength(final int maxContentLength) {
        if (maxContentLength < 1) throw new IllegalStateException("max content length cannot be less than 1 (bytes)");
        this.maxContentLength = maxContentLength;
        return this;
    }

    /**
     * Get maximum content length
     *
     * @return Maximum content length
     * @see #setMaxContentLength(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getMaxContentLength() {
        return maxContentLength;
    }

    /**
     * Set maximum chunked content length in {@code bytes}. Default is {@code 104,857,600}.
     *
     * @param maxChunkedContentLength Maximum chunked content length
     * @return Same {@code HttpServerConfiguration} instance
     * @apiNote This will only apply to the requests that have {@code Transfer-Encoding: chunked} header.
     * @see #setMaxChunkSize(int)
     * @see #setMaxContentLength(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setMaxChunkedContentLength(final int maxChunkedContentLength) {
        if (maxChunkedContentLength < 1) {
            throw new IllegalStateException("max chunked content length cannot be less than 1 (bytes)");
        }
        this.maxChunkedContentLength = maxChunkedContentLength;
        return this;
    }

    /**
     * Get maximum chunked content length.
     *
     * @return Maximum chunked content length
     * @see #setMaxChunkedContentLength(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getMaxChunkedContentLength() {
        return maxChunkedContentLength;
    }

    /**
     * Set maximum chunk size in {@code bytes}. Default is {@code 5,242,880}.
     *
     * @param maxChunkSize Maximum chunk size
     * @return Same {@code HttpServerConfiguration} instance
     * @apiNote Maximum chunk size means the maximum size of a single chunk that can have in chunked content.
     * @see #setMaxChunkSize(int)
     * @see #setMaxChunkedContentLength(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setMaxChunkSize(final int maxChunkSize) {
        if (maxChunkSize < 1) {
            throw new IllegalStateException("max chunk size cannot be less than 1 (bytes)");
        }
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    /**
     * Get maximum chunk size.
     *
     * @return Maximum chunk size
     * @see #setMaxChunkSize(int)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    /**
     * Add heads one-by-one to send with every response.
     *
     * @param defaultHeader Default header
     * @return Same {@code HttpServerConfiguration} instance
     * @see Header
     * @see #setDefaultHeaders(List)
     * @see HttpResponse#addHeader(Header)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration addDefaultHeader(final Header defaultHeader) {
        Objects.requireNonNull(defaultHeader);
        if (defaultHeaders == null) defaultHeaders = new NonDuplicateList<>();
        defaultHeaders.add(defaultHeader);
        return this;
    }

    /**
     * Set headers to send with every response.
     *
     * @param defaultHeaders List of default headers
     * @return Same {@code HttpServerConfiguration} instance
     * @see Header
     * @see #addDefaultHeader(Header)
     * @see HttpResponse#setHeaders(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultHeaders(final List<Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    /**
     * Get provided default headers.
     *
     * @return List of default headers
     * @see Header
     * @see #addDefaultHeader(Header)
     * @see #setDefaultHeaders(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public List<Header> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * Add authentication challenges one-by-one to send with every response.
     *
     * @param defaultAuthentication Default authentication challenge
     * @return Same {@code HttpServerConfiguration} instance
     * @see Authentication
     * @see #setDefaultAuthentications(List)
     * @see HttpResponse#addAuthentication(Authentication)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration addDefaultAuthentication(final Authentication defaultAuthentication) {
        Objects.requireNonNull(defaultAuthentication);
        if (defaultAuthentications == null) defaultAuthentications = new NonDuplicateList<>();
        defaultAuthentications.add(defaultAuthentication);
        return this;
    }

    /**
     * Set authentication challenges to send with every response.
     *
     * @param defaultAuthentications List of default authentication challenges
     * @return Same {@code HttpServerConfiguration} instance
     * @see Authentication
     * @see #addDefaultAuthentication(Authentication)
     * @see HttpResponse#setAuthentications(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultAuthentications(final List<Authentication> defaultAuthentications) {
        this.defaultAuthentications = defaultAuthentications;
        return this;
    }

    /**
     * Get provided default authentication challenges.
     *
     * @return List of default authentication challenges
     * @see Authentication
     * @see #addDefaultAuthentication(Authentication)
     * @see #setDefaultAuthentications(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public List<Authentication> getDefaultAuthentications() {
        return defaultAuthentications;
    }

    /**
     * Add cookies one-by-one to send with every response.
     *
     * @param defaultCookie Default cookie
     * @return Same {@code HttpServerConfiguration} instance
     * @see Cookie
     * @see #setDefaultCookies(List)
     * @see HttpResponse#addCookie(Cookie)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration addDefaultCookie(final Cookie defaultCookie) {
        Objects.requireNonNull(defaultCookie);
        if (defaultCookies == null) defaultCookies = new NonDuplicateList<>();
        defaultCookies.add(defaultCookie);
        return this;
    }

    /**
     * Set cookies to send with every response.
     *
     * @param defaultCookies List of default cookies
     * @return Same {@code HttpServerConfiguration} instance
     * @see Cookie
     * @see #addDefaultCookie(Cookie)
     * @see HttpResponse#setCookies(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultCookies(final List<Cookie> defaultCookies) {
        this.defaultCookies = defaultCookies;
        return this;
    }

    /**
     * Get provided default cookies.
     *
     * @return List of default cookies
     * @see Cookie
     * @see #addDefaultCookie(Cookie)
     * @see #setDefaultCookies(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public List<Cookie> getDefaultCookies() {
        return defaultCookies;
    }

    /**
     * Set cross-origin resource sharing to send with every response.
     *
     * @param defaultCorsResponse Default cross-origin resource sharing
     * @return Same {@code HttpServerConfiguration} instance
     * @see CORSResponse
     * @see HttpResponse#setCorsResponse(CORSResponse)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultCors(final CORSResponse defaultCorsResponse) {
        this.defaultCcorsResponse = defaultCorsResponse;
        return this;
    }

    /**
     * Get provided default cross-origin resource sharing.
     *
     * @return Default cross-origin resource sharing
     * @see CORSResponse
     * @see #setDefaultCors(CORSResponse)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public CORSResponse getDefaultCors() {
        return defaultCcorsResponse;
    }

    /**
     * Add reporting endpoints one-by-one to send with every response.
     *
     * @param reportingEndpoint Default reporting endpoint
     * @return Same {@code HttpServerConfiguration} instance
     * @see ReportingEndpoint
     * @see #setDefaultReportingEndpoints(List)
     * @see HttpResponse#addReportingEndpoint(ReportingEndpoint)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration addDefaultReportingEndpoint(final ReportingEndpoint reportingEndpoint) {
        Objects.requireNonNull(reportingEndpoint);
        if (reportingEndpoints == null) reportingEndpoints = new NonDuplicateList<>();
        reportingEndpoints.add(reportingEndpoint);
        return this;
    }

    /**
     * Set reporting endpoints to send with every response.
     *
     * @param reportingEndpoints List of default reporting endpoints
     * @return Same {@code HttpServerConfiguration} instance
     * @see ReportingEndpoint
     * @see #addDefaultReportingEndpoint(ReportingEndpoint)
     * @see HttpResponse#setReportingEndpoints(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultReportingEndpoints(final List<ReportingEndpoint> reportingEndpoints) {
        this.reportingEndpoints = reportingEndpoints;
        return this;
    }

    /**
     * Get provided default reporting endpoints.
     *
     * @return List of default reporting endpoints
     * @see ReportingEndpoint
     * @see #addDefaultReportingEndpoint(ReportingEndpoint)
     * @see #setDefaultReportingEndpoints(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public List<ReportingEndpoint> getDefaultReportingEndpoints() {
        return reportingEndpoints;
    }

    /**
     * Add content security policies one-by-one to send with every response.
     *
     * @param defaultContentSecurityPolicy Default content security policy
     * @return Same {@code HttpServerConfiguration} instance
     * @see ContentSecurityPolicy
     * @see #setDefaultContentSecurityPolicies(List)
     * @see HttpResponse#addContentSecurityPolicy(ContentSecurityPolicy)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration addDefaultContentSecurityPolicy(final ContentSecurityPolicy defaultContentSecurityPolicy) {
        Objects.requireNonNull(defaultContentSecurityPolicy);
        if (defaultContentSecurityPolicies == null) defaultContentSecurityPolicies = new NonDuplicateList<>();
        defaultContentSecurityPolicies.add(defaultContentSecurityPolicy);
        return this;
    }

    /**
     * Set content security policies to sed with every response.
     *
     * @param defaultContentSecurityPolicies List of default content security policies
     * @return Same {@code HttpServerConfiguration} instance
     * @see ContentSecurityPolicy
     * @see #addDefaultContentSecurityPolicy(ContentSecurityPolicy)
     * @see HttpResponse#setContentSecurityPolicies(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultContentSecurityPolicies(final List<ContentSecurityPolicy> defaultContentSecurityPolicies) {
        this.defaultContentSecurityPolicies = defaultContentSecurityPolicies;
        return this;
    }

    /**
     * Get provided default content security policies.
     *
     * @return List of default content security policies
     * @see ContentSecurityPolicy
     * @see #addDefaultContentSecurityPolicy(ContentSecurityPolicy)
     * @see #setDefaultContentSecurityPolicies(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public List<ContentSecurityPolicy> getDefaultContentSecurityPolicies() {
        return defaultContentSecurityPolicies;
    }

    /**
     * Add content security policy report-only one-by-one to send with every response.
     *
     * @param defaultContentSecurityPolicyReportOnly Default content security policy report-only
     * @return Same {@code HttpServerConfiguration} instance
     * @see ContentSecurityPolicyReportOnly
     * @see #setDefaultContentSecurityPolicyReportOnly(List)
     * @see HttpResponse#addContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration addDefaultContentSecurityPolicyReportOnly(final ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly) {
        Objects.requireNonNull(defaultContentSecurityPolicyReportOnly);
        if (this.defaultContentSecurityPolicyReportOnly == null) this.defaultContentSecurityPolicyReportOnly = new NonDuplicateList<>();
        this.defaultContentSecurityPolicyReportOnly.add(defaultContentSecurityPolicyReportOnly);
        return this;
    }

    /**
     * Set content security policy report-only to sed with every response.
     *
     * @param defaultContentSecurityPolicyReportOnly List of default content security policy report-only
     * @return Same {@code HttpServerConfiguration} instance
     * @see ContentSecurityPolicyReportOnly
     * @see #addDefaultContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see HttpResponse#setContentSecurityPolicyReportOnly(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultContentSecurityPolicyReportOnly(final List<ContentSecurityPolicyReportOnly> defaultContentSecurityPolicyReportOnly) {
        this.defaultContentSecurityPolicyReportOnly = defaultContentSecurityPolicyReportOnly;
        return this;
    }

    /**
     * Get provided default content security policy report-only.
     *
     * @return List of default content security policy report-only
     * @see ContentSecurityPolicyReportOnly
     * @see #addDefaultContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see #setDefaultContentSecurityPolicyReportOnly(List)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public List<ContentSecurityPolicyReportOnly> getDefaultContentSecurityPolicyReportOnly() {
        return defaultContentSecurityPolicyReportOnly;
    }

    /**
     * Set strict transport security to send with every response.
     *
     * @param defaultStrictTransportSecurity Default strict transport security
     * @return Same {@code HttpServerConfiguration} instance
     * @see StrictTransportSecurity
     * @see HttpResponse#setStrictTransportSecurity(StrictTransportSecurity)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultStrictTransportSecurity(final StrictTransportSecurity defaultStrictTransportSecurity) {
        this.defaultStrictTransportSecurity = defaultStrictTransportSecurity;
        return this;
    }

    /**
     * Get provided default strict transport security.
     *
     * @return Default strict transport security
     * @see StrictTransportSecurity
     * @see #setDefaultStrictTransportSecurity(StrictTransportSecurity)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public StrictTransportSecurity getDefaultStrictTransportSecurity() {
        return defaultStrictTransportSecurity;
    }

    /**
     * Set cache control to send with every response.
     *
     * @param cacheControl Default cache control
     * @return Same {@code HttpServerConfiguration} instance
     * @see CacheControl
     * @see HttpResponse#setCashControl(CacheControl)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultCacheControl(final CacheControl cacheControl) {
        this.defaultCacheControl = cacheControl;
        return this;
    }

    /**
     * Get provided default cache control.
     *
     * @return Default cache control
     * @see CacheControl
     * @see #setDefaultCacheControl(CacheControl)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public CacheControl getDefaultCacheControl() {
        return defaultCacheControl;
    }

    /**
     * Set x content type noSniff status to every response.
     *
     * @param xContentTypeOptionsNoSniff NoSniff status
     * @return Same {@code HttpServerConfiguration} instance
     * @see HttpResponse#setXContentTypeOptionsNoSniff(boolean)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setDefaultXContentTypeNoSniff(final boolean xContentTypeOptionsNoSniff) {
        this.defaultXContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    /**
     * Get provided x content type noSniff
     *
     * @return NoSniff status
     * @see #setDefaultXContentTypeNoSniff(boolean)
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public boolean isDefaultXContentTypeOptionsNoSniff() {
        return defaultXContentTypeOptionsNoSniff;
    }

    /**
     * Set detailed message to every exception redirect.
     *
     * @param addErrorMessageToResponseHeaders Message status
     * @return Same {@code HttpServerConfiguration} instance
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public HttpServerConfiguration setAddErrorMessageToResponseHeaders(boolean addErrorMessageToResponseHeaders) {
        this.addErrorMessageToResponseHeaders = addErrorMessageToResponseHeaders;
        return this;
    }

    /**
     * Get exception redirect detailed message status
     *
     * @return Message status
     * @see HttpServerConfiguration
     * @since v1.0.0
     */
    public boolean isAddErrorMessageToResponseHeaders() {
        return addErrorMessageToResponseHeaders;
    }
}
