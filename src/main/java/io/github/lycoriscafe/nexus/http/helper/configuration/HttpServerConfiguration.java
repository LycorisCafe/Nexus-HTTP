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
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication;
import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.headers.cors.CORSResponse;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ReportingEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

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
    private String databaseLocation = null;

    private boolean ignoreEndpointCases = true;
    private int maxHeaderSize = 10_240;
    private int maxHeadersPerRequest = 50;
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

    public HttpServerConfiguration(final String basePackage) {
        this.basePackage = Objects.requireNonNull(basePackage);
    }

    public String getBasePackage() {
        return basePackage;
    }

    public HttpServerConfiguration setPort(final int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }

    public HttpServerConfiguration setBacklog(final int backlog) {
        this.backlog = backlog;
        return this;
    }

    public int getBacklog() {
        return backlog;
    }

    public HttpServerConfiguration setInetAddress(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public HttpServerConfiguration setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public HttpServerConfiguration setThreadType(final ThreadType threadType) {
        this.threadType = Objects.requireNonNull(threadType);
        return this;
    }

    public ThreadType getThreadType() {
        return threadType;
    }

    public HttpServerConfiguration setTempDirectory(final String tempDirectory) {
        Objects.requireNonNull(tempDirectory);
        if (tempDirectory.isBlank()) throw new IllegalStateException("temp directory cannot be empty/blank");
        this.tempDirectory = tempDirectory;
        return this;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }

    public HttpServerConfiguration setUrlExtension(final String urlExtension) {
        Objects.requireNonNull(urlExtension);
        this.urlExtension = urlExtension.startsWith("\\") ? urlExtension : "\\" + urlExtension;
        return this;
    }

    public String getUrlExtension() {
        return urlExtension;
    }

    public HttpServerConfiguration setStaticFilesDirectory(final String staticFilesDirectory) {
        this.staticFilesDirectory = staticFilesDirectory;
        return this;
    }

    public String getStaticFilesDirectory() {
        return staticFilesDirectory;
    }

    public HttpServerConfiguration setDatabaseLocation(final String databaseLocation) {
        this.databaseLocation = databaseLocation;
        return this;
    }

    public String getDatabaseLocation() {
        return databaseLocation;
    }

    public HttpServerConfiguration setIgnoreEndpointCases(final boolean ignoreEndpointCases) {
        this.ignoreEndpointCases = ignoreEndpointCases;
        return this;
    }

    public boolean isIgnoreEndpointCases() {
        return ignoreEndpointCases;
    }

    public HttpServerConfiguration setMaxHeaderSize(final int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
        return this;
    }

    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public HttpServerConfiguration setMaxHeadersPerRequest(final int maxHeadersPerRequest) {
        if (maxHeadersPerRequest < 2) {
            throw new IllegalStateException("max headers per request cannot be less than 2");
        }
        this.maxHeadersPerRequest = maxHeadersPerRequest;
        return this;
    }

    public int getMaxHeadersPerRequest() {
        return maxHeadersPerRequest;
    }

    public HttpServerConfiguration setMaxIncomingConnections(final int maxIncomingConnections) {
        if (maxIncomingConnections < 1) {
            throw new IllegalStateException("max incoming connection count cannot be less than 1");
        }
        this.maxIncomingConnections = maxIncomingConnections;
        return this;
    }

    public int getMaxIncomingConnections() {
        return maxIncomingConnections;
    }

    public HttpServerConfiguration setMaxContentLength(final int maxContentLength) {
        if (maxContentLength < 1) {
            throw new IllegalStateException("max content length cannot be less than 1 (bytes)");
        }
        this.maxContentLength = maxContentLength;
        return this;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public HttpServerConfiguration setMaxChunkedContentLength(final int maxChunkedContentLength) {
        if (maxChunkedContentLength < 1) {
            throw new IllegalStateException("max chunked content length cannot be less than 1 (bytes)");
        }
        this.maxChunkedContentLength = maxChunkedContentLength;
        return this;
    }

    public int getMaxChunkedContentLength() {
        return maxChunkedContentLength;
    }

    public HttpServerConfiguration setMaxChunkSize(final int maxChunkSize) {
        if (maxChunkSize < 1) {
            throw new IllegalStateException("max chunk size cannot be less than 1 (bytes)");
        }
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public HttpServerConfiguration addDefaultHeader(final Header defaultHeader) {
        Objects.requireNonNull(defaultHeader);
        if (defaultHeaders == null) defaultHeaders = new NonDuplicateList<>();
        defaultHeaders.add(defaultHeader);
        return this;
    }

    public HttpServerConfiguration setDefaultHeaders(final List<Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public List<Header> getDefaultHeaders() {
        return defaultHeaders;
    }

    public HttpServerConfiguration addDefaultAuthentication(final Authentication defaultAuthentication) {
        Objects.requireNonNull(defaultAuthentication);
        if (defaultAuthentications == null) defaultAuthentications = new NonDuplicateList<>();
        defaultAuthentications.add(defaultAuthentication);
        return this;
    }

    public HttpServerConfiguration setDefaultAuthentications(final List<Authentication> defaultAuthentications) {
        this.defaultAuthentications = defaultAuthentications;
        return this;
    }

    public List<Authentication> getDefaultAuthentications() {
        return defaultAuthentications;
    }

    public HttpServerConfiguration addDefaultCookie(final Cookie defaultCookie) {
        Objects.requireNonNull(defaultCookie);
        if (defaultCookies == null) defaultCookies = new NonDuplicateList<>();
        defaultCookies.add(defaultCookie);
        return this;
    }

    public HttpServerConfiguration setDefaultCookies(final List<Cookie> defaultCookies) {
        this.defaultCookies = defaultCookies;
        return this;
    }

    public List<Cookie> getDefaultCookies() {
        return defaultCookies;
    }

    public HttpServerConfiguration setDefaultCors(final CORSResponse defaultCorsResponse) {
        this.defaultCcorsResponse = defaultCorsResponse;
        return this;
    }

    public CORSResponse getDefaultCors() {
        return defaultCcorsResponse;
    }

    public HttpServerConfiguration addDefaultReportingEndpoint(final ReportingEndpoint reportingEndpoint) {
        Objects.requireNonNull(reportingEndpoint);
        if (reportingEndpoints == null) reportingEndpoints = new NonDuplicateList<>();
        reportingEndpoints.add(reportingEndpoint);
        return this;
    }

    public HttpServerConfiguration setDefaultReportingEndpoints(final List<ReportingEndpoint> reportingEndpoints) {
        this.reportingEndpoints = reportingEndpoints;
        return this;
    }

    public List<ReportingEndpoint> getDefaultReportingEndpoints() {
        return reportingEndpoints;
    }

    public HttpServerConfiguration addDefaultContentSecurityPolicy(final ContentSecurityPolicy defaultContentSecurityPolicy) {
        Objects.requireNonNull(defaultContentSecurityPolicy);
        if (defaultContentSecurityPolicies == null) defaultContentSecurityPolicies = new NonDuplicateList<>();
        defaultContentSecurityPolicies.add(defaultContentSecurityPolicy);
        return this;
    }

    public HttpServerConfiguration setDefaultContentSecurityPolicies(final List<ContentSecurityPolicy> defaultContentSecurityPolicies) {
        this.defaultContentSecurityPolicies = defaultContentSecurityPolicies;
        return this;
    }

    public List<ContentSecurityPolicy> getDefaultContentSecurityPolicies() {
        return defaultContentSecurityPolicies;
    }

    public HttpServerConfiguration addDefaultContentSecurityPolicyReportOnly(final ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly) {
        Objects.requireNonNull(defaultContentSecurityPolicyReportOnly);
        if (this.defaultContentSecurityPolicyReportOnly == null) this.defaultContentSecurityPolicyReportOnly = new NonDuplicateList<>();
        this.defaultContentSecurityPolicyReportOnly.add(defaultContentSecurityPolicyReportOnly);
        return this;
    }

    public HttpServerConfiguration setDefaultContentSecurityPolicyReportOnly(final List<ContentSecurityPolicyReportOnly> defaultContentSecurityPolicyReportOnly) {
        this.defaultContentSecurityPolicyReportOnly = defaultContentSecurityPolicyReportOnly;
        return this;
    }

    public List<ContentSecurityPolicyReportOnly> getDefaultContentSecurityPolicyReportOnly() {
        return defaultContentSecurityPolicyReportOnly;
    }

    public HttpServerConfiguration setDefaultStrictTransportSecurity(final StrictTransportSecurity defaultStrictTransportSecurity) {
        this.defaultStrictTransportSecurity = defaultStrictTransportSecurity;
        return this;
    }

    public StrictTransportSecurity getDefaultStrictTransportSecurity() {
        return defaultStrictTransportSecurity;
    }

    public HttpServerConfiguration setDefaultCacheControl(final CacheControl cacheControl) {
        this.defaultCacheControl = cacheControl;
        return this;
    }

    public CacheControl getDefaultCacheControl() {
        return defaultCacheControl;
    }

    public HttpServerConfiguration setDefaultXContentTypeNoSniff(final boolean xContentTypeOptionsNoSniff) {
        this.defaultXContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    public boolean isDefaultXContentTypeOptionsNoSniff() {
        return defaultXContentTypeOptionsNoSniff;
    }

    public HttpServerConfiguration setAddErrorMessageToResponseHeaders(boolean addErrorMessageToResponseHeaders) {
        this.addErrorMessageToResponseHeaders = addErrorMessageToResponseHeaders;
        return this;
    }

    public boolean isAddErrorMessageToResponseHeaders() {
        return addErrorMessageToResponseHeaders;
    }
}
