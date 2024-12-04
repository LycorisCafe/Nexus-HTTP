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

package io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes;

import io.github.lycoriscafe.nexus.http.core.headers.Header;
import io.github.lycoriscafe.nexus.http.core.headers.auth.Authentication;
import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.headers.cors.CrossOriginResourceSharing;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ReportingEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;

public final class HttpResponse {
    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final long requestId;
    private final RequestConsumer requestConsumer;

    private final HttpStatusCode httpStatusCode;
    private HashSet<Header> headers;
    private HashSet<Cookie> cookies;
    private HashSet<ReportingEndpoint> reportingEndpoints;
    private HashSet<ContentSecurityPolicy> contentSecurityPolicies;
    private HashSet<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnly;
    private StrictTransportSecurity strictTransportSecurity;
    private boolean xContentTypeOptionsNoSniff;
    private CrossOriginResourceSharing crossOriginResourceSharing;
    private HashSet<Authentication> authentications;
    private CacheControl cacheControl;
    private Content content;

    private boolean dropConnection;

    public HttpResponse(final long requestId,
                        final RequestConsumer requestConsumer,
                        final HttpStatusCode httpStatusCode) {
        if (requestId < 0) {
            throw new IllegalStateException("invalid response id passed");
        }
        if (requestConsumer == null) {
            throw new NullPointerException("invalid request consumer passed");
        }
        if (httpStatusCode == null) {
            throw new NullPointerException("invalid httpStatusCode passed");
        }

        this.requestId = requestId;
        this.requestConsumer = requestConsumer;
        this.httpStatusCode = httpStatusCode;

        headers = requestConsumer.getServerConfiguration().getDefaultHeaders();
        cookies = requestConsumer.getServerConfiguration().getDefaultCookies();
        contentSecurityPolicies = requestConsumer.getServerConfiguration().getDefaultContentSecurityPolicies();
        contentSecurityPolicyReportOnly =
                requestConsumer.getServerConfiguration().getDefaultContentSecurityPolicyReportOnly();
        strictTransportSecurity = requestConsumer.getServerConfiguration().getDefaultStrictTransportSecurity();
        xContentTypeOptionsNoSniff = requestConsumer.getServerConfiguration().isXContentTypeOptionsNoSniff();
        crossOriginResourceSharing = requestConsumer.getServerConfiguration().getDefaultCrossOriginResourceSharing();
        cacheControl = requestConsumer.getServerConfiguration().getDefaultCacheControl();
    }

    public long getRequestId() {
        return requestId;
    }

    public RequestConsumer getRequestConsumer() {
        return requestConsumer;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public HttpResponse setHeader(final Header header) {
        if (header == null) {
            headers = null;
            return this;
        }

        if (headers == null) headers = new HashSet<>();
        headers.add(header);
        return this;
    }

    public HttpResponse setHeaders(final HashSet<Header> headers) {
        this.headers = headers;
        return this;
    }

    public List<Header> getHeaders() {
        if (headers == null) return null;
        return headers.stream().toList();
    }

    public HttpResponse setCookie(final Cookie cookie) {
        if (cookie == null) {
            this.cookies = null;
            return this;
        }

        if (cookies == null) cookies = new HashSet<>();
        cookies.add(cookie);
        return this;
    }

    public HttpResponse setCookies(final HashSet<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public List<Cookie> getCookies() {
        if (cookies == null) return null;
        return cookies.stream().toList();
    }

    public HttpResponse setReportingEndpoint(final ReportingEndpoint reportingEndpoint) {
        if (reportingEndpoint == null) {
            reportingEndpoints = null;
            return this;
        }

        if (reportingEndpoints == null) reportingEndpoints = new HashSet<>();
        reportingEndpoints.add(reportingEndpoint);
        return this;
    }

    public HttpResponse setReportingEndpoints(final HashSet<ReportingEndpoint> reportingEndpoints) {
        this.reportingEndpoints = reportingEndpoints;
        return this;
    }

    public List<ReportingEndpoint> getReportingEndpoints() {
        if (reportingEndpoints == null) return null;
        return reportingEndpoints.stream().toList();
    }

    public HttpResponse setContentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        if (contentSecurityPolicy == null) {
            contentSecurityPolicies = null;
            return this;
        }

        if (contentSecurityPolicies == null) contentSecurityPolicies = new HashSet<>();
        contentSecurityPolicies.add(contentSecurityPolicy);
        return this;
    }

    public HttpResponse setContentSecurityPolicies(final HashSet<ContentSecurityPolicy> contentSecurityPolicies) {
        this.contentSecurityPolicies = contentSecurityPolicies;
        return this;
    }

    public List<ContentSecurityPolicy> getContentSecurityPolicies() {
        if (contentSecurityPolicies == null) return null;
        return contentSecurityPolicies.stream().toList();
    }

    public HttpResponse setContentSecurityPolicyReportOnly(final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        if (contentSecurityPolicyReportOnly == null) {
            this.contentSecurityPolicyReportOnly = null;
            return this;
        }

        if (this.contentSecurityPolicyReportOnly == null) this.contentSecurityPolicyReportOnly = new HashSet<>();
        this.contentSecurityPolicyReportOnly.add(contentSecurityPolicyReportOnly);
        return this;
    }

    public HttpResponse setContentSecurityPolicyReportOnly(final HashSet<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnly) {
        this.contentSecurityPolicyReportOnly = contentSecurityPolicyReportOnly;
        return this;
    }

    public List<ContentSecurityPolicyReportOnly> getContentSecurityPolicyReportOnly() {
        if (contentSecurityPolicyReportOnly == null) return null;
        return contentSecurityPolicyReportOnly.stream().toList();
    }

    public HttpResponse setStrictTransportSecurity(final StrictTransportSecurity strictTransportSecurity) {
        this.strictTransportSecurity = strictTransportSecurity;
        return this;
    }

    public StrictTransportSecurity getStrictTransportSecurity() {
        return strictTransportSecurity;
    }

    public HttpResponse setXContentTypeOptionsNoSniff(final boolean xContentTypeOptionsNoSniff) {
        this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    public boolean isXContentTypeOptionsNoSniff() {
        return xContentTypeOptionsNoSniff;
    }

    public HttpResponse setCrossOriginResourceSharing(final CrossOriginResourceSharing crossOriginResourceSharing) {
        this.crossOriginResourceSharing = crossOriginResourceSharing;
        return this;
    }

    public CrossOriginResourceSharing getCrossOriginResourceSharing() {
        return crossOriginResourceSharing;
    }

    public HttpResponse setAuthentication(final Authentication authentication) {
        if (authentication == null) {
            authentications = null;
            return this;
        }

        if (authentications == null) authentications = new HashSet<>();
        authentications.add(authentication);
        return this;
    }

    public HttpResponse setAuthentications(final HashSet<Authentication> authentications) {
        this.authentications = authentications;
        return this;
    }

    public List<Authentication> getAuthentications() {
        if (authentications == null) return null;
        return authentications.stream().toList();
    }

    public HttpResponse setCashControl(final CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public HttpResponse setContent(final Content content) {
        this.content = content;
        return this;
    }

    public Content getContent() {
        return content;
    }

    public HttpResponse setDropConnection(final boolean dropConnection) {
        this.dropConnection = dropConnection;
        return this;
    }

    public boolean isDropConnection() {
        return dropConnection;
    }

    public String finalizeResponse() {
        try {
            StringBuilder output =
                    new StringBuilder().append("HTTP/1.1").append(" ").append(httpStatusCode.getStatusCode())
                            .append("\r\n").append("Server:").append(" ").append("nexus-http/1.0.0").append("\r\n")
                            .append("Connection:").append(" ").append("keep-alive").append("\r\n")

                            .append(Header.processOutgoingHeaders(getHeaders()))
                            .append(Cookie.processOutgoingCookies(getCookies()))
                            .append(ReportingEndpoint.processOutgoingReportingEndpoints(getReportingEndpoints()))
                            .append(ContentSecurityPolicy.processOutgoingCsp(getContentSecurityPolicies(),
                                    false))
                            .append(ContentSecurityPolicyReportOnly.processOutgoingCsp(
                                    getContentSecurityPolicyReportOnly(), true))
                            .append(StrictTransportSecurity.processOutgoingHSTS(getStrictTransportSecurity()))
                            .append(CrossOriginResourceSharing.processOutgoingCORS(getCrossOriginResourceSharing()))
                            .append(Authentication.processOutgoingAuthentication(getAuthentications()))
                            .append(CacheControl.processOutgoingCacheControl(getCacheControl()))
                            .append(Content.WriteOperations.processOutgoingContent(getContent()));

            if (isXContentTypeOptionsNoSniff()) {
                output.append("X-Content-Type-Options: nosniff").append("\r\n");
            }

            return output.append("\r\n").toString();
        } catch (Exception e) {
            requestConsumer.dropConnection(requestId, HttpStatusCode.INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}