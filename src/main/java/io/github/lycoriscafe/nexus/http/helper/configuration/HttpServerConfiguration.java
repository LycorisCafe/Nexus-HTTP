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
import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
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

    private boolean ignoreEndpointCases = true;
    private int maxHeadersPerRequest = 50;
    private int maxIncomingConnections = 100;
    private int pipelineParallelProcesses = 1;
    private int maxContentLength = 5_242_880;
    private int maxChunkedContentLength = 104_857_600;
    private int maxChunkSize = 5_242_880;

    private HashSet<Header> defaultHeaders = null;
    private HashSet<WWWAuthentication> defaultAuthentications = null;
    private HashSet<Cookie> defaultCookies = null;
    private CrossOriginResourceSharing defaultCrossOriginResourceSharing = null;
    private ContentSecurityPolicy defaultContentSecurityPolicy = null;
    private ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly = null;
    private StrictTransportSecurity defaultStrictTransportSecurity = null;
    private CacheControl defaultCacheControl = null;
    private boolean xContentTypeOptionsNoSniff;

    public HttpServerConfiguration(final String basePackage) {
        this.basePackage = basePackage;
    }

    public HttpServerConfiguration port(final int port) {
        this.port = port;
        return this;
    }

    public HttpServerConfiguration backlog(final int backlog) {
        this.backlog = backlog;
        return this;
    }

    public HttpServerConfiguration inetAddress(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }

    public HttpServerConfiguration connectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public HttpServerConfiguration threadType(final ThreadType threadType)
            throws HttpServerConfigurationException {
        if (threadType == null) {
            throw new HttpServerConfigurationException("thread type cannot be null");
        }
        this.threadType = threadType;
        return this;
    }

    public HttpServerConfiguration tempDirectory(final String tempDirectory) {
        if (tempDirectory != null) {
            this.tempDirectory = tempDirectory;
        }
        return this;
    }

    public HttpServerConfiguration staticFilesDirectory(final String staticFilesDirectory) {
        this.staticFilesDirectory = staticFilesDirectory;
        return this;
    }

    public HttpServerConfiguration databaseLocation(final String databaseLocation) {
        this.databaseLocation = databaseLocation;
        return this;
    }

    public HttpServerConfiguration ignoreEndpointCases(final boolean ignoreEndpointCases) {
        this.ignoreEndpointCases = ignoreEndpointCases;
        return this;
    }

    public HttpServerConfiguration maxHeadersPerRequest(final int maxHeadersPerRequest)
            throws HttpServerConfigurationException {
        if (maxHeadersPerRequest < 2) {
            throw new HttpServerConfigurationException("max headers per request cannot be less than 2");
        }
        this.maxHeadersPerRequest = maxHeadersPerRequest;
        return this;
    }

    public HttpServerConfiguration maxIncomingConnections(final int maxIncomingConnections)
            throws HttpServerConfigurationException {
        if (maxIncomingConnections < 1) {
            throw new HttpServerConfigurationException("max incoming connection count cannot be less than 1");
        }
        this.maxIncomingConnections = maxIncomingConnections;
        return this;
    }

    public HttpServerConfiguration pipelineParallelProcesses(final int pipelineParallelProcesses)
            throws HttpServerConfigurationException {
        if (pipelineParallelProcesses < 1) {
            throw new HttpServerConfigurationException("pipeline processes count cannot be less than 1");
        }
        this.pipelineParallelProcesses = pipelineParallelProcesses;
        return this;
    }

    public HttpServerConfiguration maxContentLength(final int maxContentLength)
            throws HttpServerConfigurationException {
        if (maxContentLength < 1) {
            throw new HttpServerConfigurationException("max content length cannot be less than 1 (bytes)");
        }
        this.maxContentLength = maxContentLength;
        return this;
    }

    public HttpServerConfiguration maxChunkedContentLength(final int maxChunkedContentLength)
            throws HttpServerConfigurationException {
        if (maxChunkedContentLength < 1) {
            throw new HttpServerConfigurationException("max chunked content length cannot be less than 1 (bytes)");
        }
        this.maxChunkedContentLength = maxChunkedContentLength;
        return this;
    }

    public HttpServerConfiguration maxChunkSize(final int maxChunkSize) throws HttpServerConfigurationException {
        if (maxChunkSize < 1) {
            throw new HttpServerConfigurationException("max chunk size cannot be less than 1 (bytes)");
        }
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    public HttpServerConfiguration defaultHeaders(final HashSet<Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public HttpServerConfiguration defaultAuthentications(final HashSet<WWWAuthentication> defaultAuthentications) {
        this.defaultAuthentications = defaultAuthentications;
        return this;
    }

    public HttpServerConfiguration defaultCookies(final HashSet<Cookie> defaultCookies) {
        this.defaultCookies = defaultCookies;
        return this;
    }

    public HttpServerConfiguration defaultCrossOriginResourceSharing(
            final CrossOriginResourceSharing defaultCrossOriginResourceSharing) {
        this.defaultCrossOriginResourceSharing = defaultCrossOriginResourceSharing;
        return this;
    }

    public HttpServerConfiguration defaultContentSecurityPolicy(
            final ContentSecurityPolicy defaultContentSecurityPolicy) {
        this.defaultContentSecurityPolicy = defaultContentSecurityPolicy;
        return this;
    }

    public HttpServerConfiguration defaultContentSecurityPolicyReportOnly(
            final ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly) {
        this.defaultContentSecurityPolicyReportOnly = defaultContentSecurityPolicyReportOnly;
        return this;
    }

    public HttpServerConfiguration defaultStrictTransportSecurity(
            final StrictTransportSecurity defaultStrictTransportSecurity) {
        this.defaultStrictTransportSecurity = defaultStrictTransportSecurity;
        return this;
    }

    public HttpServerConfiguration defaultCacheControl(final CacheControl cacheControl) {
        this.defaultCacheControl = cacheControl;
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

    public int getMaxChunkSize() {
        return maxChunkSize;
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

    public CacheControl getDefaultCacheControl() {
        return defaultCacheControl;
    }

    public boolean isxContentTypeOptionsNoSniff() {
        return xContentTypeOptionsNoSniff;
    }
}
