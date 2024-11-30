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

import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.auth.WWWAuthentication;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.headers.cors.CrossOriginResourceSharing;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;

import java.net.InetAddress;
import java.util.HashSet;

public final class HttpServerConfiguration {
    private int port = 0;
    private int backlog = 0;
    private InetAddress inetAddress = null;
    private int connectionTimeout = 60_000;
    private ThreadType threadType = ThreadType.VIRTUAL;

    private final String basePackage;
    private String tempDirectory = "NexusTemp";
    private String staticFilesDirectory = "NexusStatics";
    private String databaseLocation = null;

    private boolean ignoreEndpointCases;
    private int maxHeadersPerRequest = 10;
    private int maxIncomingConnections = 100;
    private int pipelineParallelProcesses = 1;
    private int maxContentLength = 5_242_880;
    private int maxChunkedContentLength = 104_857_600;

    private HashSet<Header> defaultHeaders = null;
    private HashSet<WWWAuthentication> defaultAuthentications = null;
    private HashSet<Cookie> defaultCookies = null;
    private CrossOriginResourceSharing defaultCrossOriginResourceSharing = null;
    private ContentSecurityPolicy defaultContentSecurityPolicy = null;
    private ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly = null;
    private StrictTransportSecurity defaultStrictTransportSecurity = null;
    private boolean xContentTypeOptionsNoSniff;

    public HttpServerConfiguration(String basePackage) {
        this.basePackage = basePackage;
    }

    public HttpServerConfiguration port(int port) {
        this.port = port;
        return this;
    }

    public HttpServerConfiguration backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public HttpServerConfiguration inetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }

    public HttpServerConfiguration connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public HttpServerConfiguration threadType(ThreadType threadType)
            throws HttpServerConfigurationException {
        if (threadType == null) {
            throw new HttpServerConfigurationException("thread type cannot be null");
        }
        this.threadType = threadType;
        return this;
    }

    public HttpServerConfiguration tempDirectory(String tempDirectory) {
        if (tempDirectory != null) {
            this.tempDirectory = tempDirectory;
        }
        return this;
    }

    public HttpServerConfiguration staticFilesDirectory(String staticFilesDirectory) {
        this.staticFilesDirectory = staticFilesDirectory;
        return this;
    }

    public HttpServerConfiguration databaseLocation(String databaseLocation) {
        this.databaseLocation = databaseLocation;
        return this;
    }

    public HttpServerConfiguration ignoreEndpointCases(boolean ignoreEndpointCases) {
        this.ignoreEndpointCases = ignoreEndpointCases;
        return this;
    }

    public HttpServerConfiguration maxHeadersPerRequest(int maxHeadersPerRequest)
            throws HttpServerConfigurationException {
        if (maxHeadersPerRequest < 2) {
            throw new HttpServerConfigurationException("max headers per request cannot be less than 2");
        }
        this.maxHeadersPerRequest = maxHeadersPerRequest;
        return this;
    }

    public HttpServerConfiguration maxIncomingConnections(int maxIncomingConnections)
            throws HttpServerConfigurationException {
        if (maxIncomingConnections < 1) {
            throw new HttpServerConfigurationException("max incoming connection count cannot be less than 1");
        }
        this.maxIncomingConnections = maxIncomingConnections;
        return this;
    }

    public HttpServerConfiguration pipelineParallelProcesses(int pipelineParallelProcesses)
            throws HttpServerConfigurationException {
        if (pipelineParallelProcesses < 1) {
            throw new HttpServerConfigurationException("pipeline processes count cannot be less than 1");
        }
        this.pipelineParallelProcesses = pipelineParallelProcesses;
        return this;
    }

    public HttpServerConfiguration maxContentLength(int maxContentLength)
            throws HttpServerConfigurationException {
        if (maxContentLength < 1) {
            throw new HttpServerConfigurationException("max content length cannot be less than 1 (bytes)");
        }
        this.maxContentLength = maxContentLength;
        return this;
    }

    public HttpServerConfiguration maxChunkedContentLength(int maxChunkedContentLength)
            throws HttpServerConfigurationException {
        if (maxChunkedContentLength < 1) {
            throw new HttpServerConfigurationException("max chunked content length cannot be less than 1 (bytes)");
        }
        this.maxChunkedContentLength = maxChunkedContentLength;
        return this;
    }

    public HttpServerConfiguration defaultHeaders(HashSet<Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public HttpServerConfiguration defaultAuthentications(HashSet<WWWAuthentication> defaultAuthentications) {
        this.defaultAuthentications = defaultAuthentications;
        return this;
    }

    public HttpServerConfiguration defaultCookies(HashSet<Cookie> defaultCookies) {
        this.defaultCookies = defaultCookies;
        return this;
    }

    public HttpServerConfiguration defaultCrossOriginResourceSharing(
            CrossOriginResourceSharing defaultCrossOriginResourceSharing) {
        this.defaultCrossOriginResourceSharing = defaultCrossOriginResourceSharing;
        return this;
    }

    public HttpServerConfiguration defaultContentSecurityPolicy(ContentSecurityPolicy defaultContentSecurityPolicy) {
        this.defaultContentSecurityPolicy = defaultContentSecurityPolicy;
        return this;
    }

    public HttpServerConfiguration defaultContentSecurityPolicyReportOnly(
            ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly) {
        this.defaultContentSecurityPolicyReportOnly = defaultContentSecurityPolicyReportOnly;
        return this;
    }

    public HttpServerConfiguration defaultStrictTransportSecurity(StrictTransportSecurity defaultStrictTransportSecurity) {
        this.defaultStrictTransportSecurity = defaultStrictTransportSecurity;
        return this;
    }

    public HttpServerConfiguration xContentTypeNoSniff(final boolean xContentTypeOptionsNoSniff) {
        this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    public int getPort() {
        return port;
    }

    public int getBacklog() {
        return backlog;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ThreadType getThreadType() {
        return threadType;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }

    public String getStaticFilesDirectory() {
        return staticFilesDirectory;
    }

    public String getDatabaseLocation() {
        return databaseLocation;
    }

    public boolean isIgnoreEndpointCases() {
        return ignoreEndpointCases;
    }

    public int getMaxHeadersPerRequest() {
        return maxHeadersPerRequest;
    }

    public int getMaxIncomingConnections() {
        return maxIncomingConnections;
    }

    public int getPipelineParallelProcesses() {
        return pipelineParallelProcesses;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public int getMaxChunkedContentLength() {
        return maxChunkedContentLength;
    }

    public HashSet<Header> getDefaultHeaders() {
        return defaultHeaders;
    }

    public HashSet<WWWAuthentication> getDefaultAuthentications() {
        return defaultAuthentications;
    }

    public HashSet<Cookie> getDefaultCookies() {
        return defaultCookies;
    }

    public CrossOriginResourceSharing getDefaultCrossOriginResourceSharing() {
        return defaultCrossOriginResourceSharing;
    }

    public ContentSecurityPolicy getDefaultContentSecurityPolicy() {
        return defaultContentSecurityPolicy;
    }

    public ContentSecurityPolicyReportOnly getDefaultContentSecurityPolicyReportOnly() {
        return defaultContentSecurityPolicyReportOnly;
    }

    public StrictTransportSecurity getDefaultStrictTransportSecurity() {
        return defaultStrictTransportSecurity;
    }

    public boolean isxContentTypeOptionsNoSniff() {
        return xContentTypeOptionsNoSniff;
    }
}
