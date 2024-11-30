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
import io.github.lycoriscafe.nexus.http.core.headers.auth.WWWAuthentication;
import io.github.lycoriscafe.nexus.http.core.headers.cache.CacheControl;
import io.github.lycoriscafe.nexus.http.core.headers.content.Content;
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.headers.cors.CrossOriginResourceSharing;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;

public final class HttpResponse {
    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final HttpRequest httpRequest;

    private final HttpStatusCode httpStatusCode;
    private HashSet<Header> headers;
    private HashSet<Cookie> cookies;
    private ContentSecurityPolicy contentSecurityPolicy;
    private ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly;
    private StrictTransportSecurity strictTransportSecurity;
    private boolean xContentTypeOptionsNoSniff;
    private CrossOriginResourceSharing crossOriginResourceSharing;
    private HashSet<WWWAuthentication> wwwAuthentications;
    private CacheControl cacheControl;
    private Content content = null;

    public HttpResponse(final HttpRequest httpRequest,
                        final HttpStatusCode httpStatusCode) {
        if (httpRequest == null) {
            throw new NullPointerException("invalid http request passed");
        }
        if (httpStatusCode == null) {
            throw new NullPointerException("invalid httpStatusCode passed");
        }

        this.httpRequest = httpRequest;
        this.httpStatusCode = httpStatusCode;

        headers = httpRequest.getRequestConsumer().getServerConfiguration().getDefaultHeaders();
        cookies = httpRequest.getRequestConsumer().getServerConfiguration().getDefaultCookies();
        contentSecurityPolicy =
                httpRequest.getRequestConsumer().getServerConfiguration().getDefaultContentSecurityPolicy();
        contentSecurityPolicyReportOnly =
                httpRequest.getRequestConsumer().getServerConfiguration().getDefaultContentSecurityPolicyReportOnly();
        strictTransportSecurity =
                httpRequest.getRequestConsumer().getServerConfiguration().getDefaultStrictTransportSecurity();
        xContentTypeOptionsNoSniff =
                httpRequest.getRequestConsumer().getServerConfiguration().isxContentTypeOptionsNoSniff();
        crossOriginResourceSharing =
                httpRequest.getRequestConsumer().getServerConfiguration().getDefaultCrossOriginResourceSharing();
        wwwAuthentications = httpRequest.getRequestConsumer().getServerConfiguration().getDefaultAuthentications();
        cacheControl = httpRequest.getRequestConsumer().getServerConfiguration().getDefaultCacheControl();
    }

    public HttpResponse header(final Header header) {
        if (header == null) {
            logger.atDebug().log("header with null value detected, resetting global settings");
            headers = null;
            return this;
        }
        if (headers == null) {
            headers = new HashSet<>();
        }
        headers.add(header);
        return this;
    }

    public HttpResponse cookie(final Cookie cookie) {
        if (cookie == null) {
            logger.atDebug().log("cookie with null value detected, resetting global settings");
            cookies = null;
            return this;
        }
        if (cookies == null) {
            cookies = new HashSet<>();
        }
        cookies.add(cookie);
        return this;
    }

    public HttpResponse contentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        if (contentSecurityPolicy == null) {
            logger.atDebug().log("content security policy with null value detected, resetting global settings");
            this.contentSecurityPolicy = null;
            return this;
        }
        this.contentSecurityPolicy = contentSecurityPolicy;
        return this;
    }

    public HttpResponse contentSecurityPolicy(final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        if (contentSecurityPolicyReportOnly == null) {
            logger.atDebug()
                    .log("content security policy report only with null value detected, resetting global settings");
            this.contentSecurityPolicyReportOnly = null;
            return this;
        }
        this.contentSecurityPolicyReportOnly = contentSecurityPolicyReportOnly;
        return this;
    }

    public HttpResponse strictTransportSecurity(final StrictTransportSecurity strictTransportSecurity) {
        if (strictTransportSecurity == null) {
            logger.atDebug().log("strict transport security with null value detected, resetting global settings");
            this.strictTransportSecurity = null;
            return this;
        }
        this.strictTransportSecurity = strictTransportSecurity;
        return this;
    }

    public HttpResponse xContentTypeOptionsNoSniff(final boolean xContentTypeOptionsNoSniff) {
        this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    public HttpResponse crossOriginResourceSharing(final CrossOriginResourceSharing crossOriginResourceSharing) {
        if (crossOriginResourceSharing == null) {
            logger.atDebug().log("cross origin resource sharing with null value detected, resetting global settings");
            this.crossOriginResourceSharing = null;
            return this;
        }
        this.crossOriginResourceSharing = crossOriginResourceSharing;
        return this;
    }

    public HttpResponse wwwAuthentication(final WWWAuthentication wwwAuthentication) {
        if (wwwAuthentication == null) {
            logger.atDebug().log("www authentication with null value detected, resetting global settings");
            this.wwwAuthentications = null;
            return this;
        }
        if (wwwAuthentications == null) {
            wwwAuthentications = new HashSet<>();
        }
        wwwAuthentications.add(wwwAuthentication);
        return this;
    }

    public HttpResponse cashControl(final CacheControl cacheControl) {
        if (cacheControl == null) {
            logger.atDebug().log("cache control with null value detected, resetting global settings");
            this.cacheControl = null;
            return this;
        }
        this.cacheControl = cacheControl;
        return this;
    }

    public HttpResponse content(final Content content) {
        if (content == null) {
            logger.atDebug().log("content with null value detected, resetting global settings");
            return this;
        }
        this.content = content;
        return this;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public List<Header> getHeaders() {
        return headers.stream().toList();
    }

    public List<Cookie> getCookies() {
        return cookies.stream().toList();
    }

    public ContentSecurityPolicy getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    public ContentSecurityPolicyReportOnly getContentSecurityPolicyReportOnly() {
        return contentSecurityPolicyReportOnly;
    }

    public StrictTransportSecurity getStrictTransportSecurity() {
        return strictTransportSecurity;
    }

    public boolean isXContentTypeOptionsNoSniff() {
        return xContentTypeOptionsNoSniff;
    }

    public CrossOriginResourceSharing getCrossOriginResourceSharing() {
        return crossOriginResourceSharing;
    }

    public List<WWWAuthentication> getWWWAuthentications() {
        return wwwAuthentications.stream().toList();
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public Content getContent() {
        return content;
    }

    public String finalizeResponse() {
        StringBuilder output =
                new StringBuilder().append("HTTP/1.1").append(" ").append(httpStatusCode.getStatusCode()).append("\r\n")
                        .append("Server:").append(" ").append("nexus-http/1.0.0").append("\r\n").append("Connection:")
                        .append(" ").append("keep-alive").append("\r\n")

                        .append(Header.processOutgoingHeader(headers)).append(Cookie.processOutgoingCookies(cookies))
                        .append(ContentSecurityPolicy.processOutgoingCsp(contentSecurityPolicy,
                                contentSecurityPolicyReportOnly))
                        .append(StrictTransportSecurity.processOutgoingHSTS(strictTransportSecurity))
                        .append(CrossOriginResourceSharing.processOutgoingCORS(crossOriginResourceSharing))
                        .append(WWWAuthentication.processOutgoingAuth(wwwAuthentications))
                        .append(CacheControl.processOutgoingCacheControl(cacheControl));

        if (xContentTypeOptionsNoSniff) {
            output.append("X-Content-Type-Options: nosniff").append("\r\n");
        }

        return output.toString();
    }
}