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
import io.github.lycoriscafe.nexus.http.core.headers.cors.CrossOriginResourceSharing;
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
    private String tempDirectory = "NexusTemp";
    private String staticFilesDirectory = null;
    private String databaseLocation = null;

    private boolean ignoreEndpointCases = true;
    private int maxHeadersPerRequest = 50;
    private int maxIncomingConnections = 100;
    private int maxContentLength = 5_242_880;
    private int maxChunkedContentLength = 104_857_600;
    private int maxChunkSize = 5_242_880;

    private List<Header> defaultHeaders = null;
    private List<Authentication> defaultAuthentications = null;
    private List<Cookie> defaultCookies = null;
    private CrossOriginResourceSharing defaultCrossOriginResourceSharing = null;
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

    public HttpServerConfiguration setTempDirectory(final String tempDirectory) throws HttpServerConfigurationException {
        Objects.requireNonNull(tempDirectory);
        if (tempDirectory.isBlank()) throw new HttpServerConfigurationException("temp directory cannot be empty/blank");
        this.tempDirectory = tempDirectory;
        return this;
    }

    public String getTempDirectory() {
        return tempDirectory;
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

    public HttpServerConfiguration setMaxHeadersPerRequest(final int maxHeadersPerRequest) throws HttpServerConfigurationException {
        if (maxHeadersPerRequest < 2) {
            throw new HttpServerConfigurationException("max headers per request cannot be less than 2");
        }
        this.maxHeadersPerRequest = maxHeadersPerRequest;
        return this;
    }

    public int getMaxHeadersPerRequest() {
        return maxHeadersPerRequest;
    }

    public HttpServerConfiguration setMaxIncomingConnections(final int maxIncomingConnections) throws HttpServerConfigurationException {
        if (maxIncomingConnections < 1) {
            throw new HttpServerConfigurationException("max incoming connection count cannot be less than 1");
        }
        this.maxIncomingConnections = maxIncomingConnections;
        return this;
    }

    public int getMaxIncomingConnections() {
        return maxIncomingConnections;
    }

    public HttpServerConfiguration setMaxContentLength(final int maxContentLength) throws HttpServerConfigurationException {
        if (maxContentLength < 1) {
            throw new HttpServerConfigurationException("max content length cannot be less than 1 (bytes)");
        }
        this.maxContentLength = maxContentLength;
        return this;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public HttpServerConfiguration setMaxChunkedContentLength(final int maxChunkedContentLength) throws HttpServerConfigurationException {
        if (maxChunkedContentLength < 1) {
            throw new HttpServerConfigurationException("max chunked content length cannot be less than 1 (bytes)");
        }
        this.maxChunkedContentLength = maxChunkedContentLength;
        return this;
    }

    public int getMaxChunkedContentLength() {
        return maxChunkedContentLength;
    }

    public HttpServerConfiguration setMaxChunkSize(final int maxChunkSize) throws HttpServerConfigurationException {
        if (maxChunkSize < 1) {
            throw new HttpServerConfigurationException("max chunk size cannot be less than 1 (bytes)");
        }
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public HttpServerConfiguration setDefaultHeaders(final List<Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public List<Header> getDefaultHeaders() {
        return defaultHeaders;
    }

    public HttpServerConfiguration addDefaultAuthentication(final Authentication defaultAuthentication) {
        if (defaultAuthentications == null) defaultAuthentications = new NonDuplicateList<>();
        defaultAuthentications.add(defaultAuthentication);
        return this;
    }

    public List<Authentication> getDefaultAuthentications() {
        return defaultAuthentications;
    }

    public HttpServerConfiguration addDefaultCookies(final Cookie defaultCookie) {
        if (defaultCookies == null) defaultCookies = new NonDuplicateList<>();
        defaultCookies.add(defaultCookie);
        return this;
    }

    public List<Cookie> getDefaultCookies() {
        return defaultCookies;
    }

    public HttpServerConfiguration setDefaultCrossOriginResourceSharing(final CrossOriginResourceSharing defaultCrossOriginResourceSharing) {
        this.defaultCrossOriginResourceSharing = defaultCrossOriginResourceSharing;
        return this;
    }

    public CrossOriginResourceSharing getDefaultCrossOriginResourceSharing() {
        return defaultCrossOriginResourceSharing;
    }

    public HttpServerConfiguration addDefaultReportingEndpoints(final ReportingEndpoint reportingEndpoint) {
        if (reportingEndpoints == null) reportingEndpoints = new NonDuplicateList<>();
        reportingEndpoints.add(reportingEndpoint);
        return this;
    }

    public List<ReportingEndpoint> getDefaultReportingEndpoints() {
        return reportingEndpoints;
    }

    public HttpServerConfiguration addDefaultContentSecurityPolicies(final ContentSecurityPolicy defaultContentSecurityPolicy) {
        if (defaultContentSecurityPolicies == null) defaultContentSecurityPolicies = new NonDuplicateList<>();
        defaultContentSecurityPolicies.add(defaultContentSecurityPolicy);
        return this;
    }

    public List<ContentSecurityPolicy> getDefaultContentSecurityPolicies() {
        return defaultContentSecurityPolicies;
    }

    public HttpServerConfiguration addDefaultContentSecurityPolicyReportOnly(final ContentSecurityPolicyReportOnly defaultContentSecurityPolicyReportOnly) {
        if (this.defaultContentSecurityPolicyReportOnly == null) this.defaultContentSecurityPolicyReportOnly = new NonDuplicateList<>();
        this.defaultContentSecurityPolicyReportOnly.add(defaultContentSecurityPolicyReportOnly);
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
