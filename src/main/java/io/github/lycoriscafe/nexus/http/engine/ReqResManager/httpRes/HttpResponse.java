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
import io.github.lycoriscafe.nexus.http.core.headers.cors.CORSResponse;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ReportingEndpoint;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public final class HttpResponse {
    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final long requestId;
    private final RequestConsumer requestConsumer;

    private HttpStatusCode httpStatusCode = HttpStatusCode.OK;
    private List<Header> headers;
    private List<Cookie> cookies;
    private List<ReportingEndpoint> reportingEndpoints;
    private List<ContentSecurityPolicy> contentSecurityPolicies;
    private List<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnly;
    private StrictTransportSecurity strictTransportSecurity;
    private boolean xContentTypeOptionsNoSniff;
    private CORSResponse corsResponse;
    private List<Authentication> authentications;
    private CacheControl cacheControl;
    private Content content;

    private boolean dropConnection;

    public HttpResponse(final long requestId,
                        final RequestConsumer requestConsumer) {
        if (requestId < 0) throw new IllegalStateException("invalid response id passed");
        this.requestId = requestId;
        this.requestConsumer = Objects.requireNonNull(requestConsumer);

        headers = requestConsumer.getServerConfiguration().getDefaultHeaders();
        cookies = requestConsumer.getServerConfiguration().getDefaultCookies();
        contentSecurityPolicies = requestConsumer.getServerConfiguration().getDefaultContentSecurityPolicies();
        contentSecurityPolicyReportOnly = requestConsumer.getServerConfiguration().getDefaultContentSecurityPolicyReportOnly();
        strictTransportSecurity = requestConsumer.getServerConfiguration().getDefaultStrictTransportSecurity();
        xContentTypeOptionsNoSniff = requestConsumer.getServerConfiguration().isDefaultXContentTypeOptionsNoSniff();
        corsResponse = requestConsumer.getServerConfiguration().getDefaultCrossOriginResourceSharing();
        cacheControl = requestConsumer.getServerConfiguration().getDefaultCacheControl();
    }

    public long getRequestId() {
        return requestId;
    }

    public RequestConsumer getRequestConsumer() {
        return requestConsumer;
    }

    public HttpResponse setStatusCode(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = Objects.requireNonNull(httpStatusCode);
        return this;
    }

    public HttpStatusCode getStatusCode() {
        return httpStatusCode;
    }

    public HttpResponse setHeader(final Header header) {
        if (header == null) {
            headers = null;
            return this;
        }

        if (headers == null) headers = new NonDuplicateList<>();
        headers.add(header);
        return this;
    }

    public HttpResponse setHeaders(final List<Header> headers) {
        this.headers = headers;
        return this;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public HttpResponse setCookie(final Cookie cookie) {
        if (cookie == null) {
            this.cookies = null;
            return this;
        }

        if (cookies == null) cookies = new NonDuplicateList<>();
        cookies.add(cookie);
        return this;
    }

    public HttpResponse setCookies(final List<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public HttpResponse setReportingEndpoint(final ReportingEndpoint reportingEndpoint) {
        if (reportingEndpoint == null) {
            reportingEndpoints = null;
            return this;
        }

        if (reportingEndpoints == null) reportingEndpoints = new NonDuplicateList<>();
        reportingEndpoints.add(reportingEndpoint);
        return this;
    }

    public HttpResponse setReportingEndpoints(final List<ReportingEndpoint> reportingEndpoints) {
        this.reportingEndpoints = reportingEndpoints;
        return this;
    }

    public List<ReportingEndpoint> getReportingEndpoints() {
        return reportingEndpoints;
    }

    public HttpResponse setContentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        if (contentSecurityPolicy == null) {
            contentSecurityPolicies = null;
            return this;
        }

        if (contentSecurityPolicies == null) contentSecurityPolicies = new NonDuplicateList<>();
        contentSecurityPolicies.add(contentSecurityPolicy);
        return this;
    }

    public HttpResponse setContentSecurityPolicies(final List<ContentSecurityPolicy> contentSecurityPolicies) {
        this.contentSecurityPolicies = contentSecurityPolicies;
        return this;
    }

    public List<ContentSecurityPolicy> getContentSecurityPolicies() {
        return contentSecurityPolicies;
    }

    public HttpResponse setContentSecurityPolicyReportOnly(final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        if (contentSecurityPolicyReportOnly == null) {
            this.contentSecurityPolicyReportOnly = null;
            return this;
        }

        if (this.contentSecurityPolicyReportOnly == null)
            this.contentSecurityPolicyReportOnly = new NonDuplicateList<>();
        this.contentSecurityPolicyReportOnly.add(contentSecurityPolicyReportOnly);
        return this;
    }

    public HttpResponse setContentSecurityPolicyReportOnly(final List<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnly) {
        this.contentSecurityPolicyReportOnly = contentSecurityPolicyReportOnly;
        return this;
    }

    public List<ContentSecurityPolicyReportOnly> getContentSecurityPolicyReportOnly() {
        return contentSecurityPolicyReportOnly;
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

    public HttpResponse setCorsResponse(final CORSResponse corsResponse) {
        this.corsResponse = corsResponse;
        return this;
    }

    public CORSResponse getCorsResponse() {
        return corsResponse;
    }

    public HttpResponse setAuthentication(final Authentication authentication) {
        if (authentication == null) {
            authentications = null;
            return this;
        }

        if (authentications == null) authentications = new NonDuplicateList<>();
        authentications.add(authentication);
        return this;
    }

    public HttpResponse setAuthentications(final List<Authentication> authentications) {
        this.authentications = authentications;
        return this;
    }

    public List<Authentication> getAuthentications() {
        return authentications;
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
            StringBuilder output = new StringBuilder().append("HTTP/1.1").append(" ").append(httpStatusCode.getStatusCode())
                    .append("\r\n").append("Server:").append(" ").append("nexus-http/1.0.0").append("\r\n")
                    .append("Connection:").append(" ").append("keep-alive").append("\r\n")
                    .append(Header.parseOutgoingHeaders(getHeaders()))
                    .append(Cookie.processOutgoingCookies(getCookies()))
                    .append(ReportingEndpoint.processOutgoingReportingEndpoints(getReportingEndpoints()))
                    .append(ContentSecurityPolicy.processOutgoingCsp(getContentSecurityPolicies(), false))
                    .append(ContentSecurityPolicyReportOnly.processOutgoingCsp(getContentSecurityPolicyReportOnly(), true))
                    .append(StrictTransportSecurity.processOutgoingHSTS(getStrictTransportSecurity()))
                    .append(CORSResponse.processOutgoingCORS(getCorsResponse()))
                    .append(Authentication.processOutgoingAuthentications(getAuthentications()))
                    .append(CacheControl.processOutgoingCacheControl(getCacheControl()))
                    .append(Content.WriteOperations.processOutgoingContent(getRequestConsumer().getServerConfiguration(), getContent()));
            if (isXContentTypeOptionsNoSniff()) output.append("X-Content-Type-Options: nosniff").append("\r\n");
            return output.append("\r\n").toString();
        } catch (Exception e) {
            requestConsumer.dropConnection(requestId, HttpStatusCode.INTERNAL_SERVER_ERROR,
                    "error while parsing http response");
            return null;
        }
    }
}