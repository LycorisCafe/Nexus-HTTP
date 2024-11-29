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

import io.github.lycoriscafe.nexus.http.core.headers.auth.WWWAuthentication;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class HttpResponse {
    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private boolean aborted = false;
    private final HttpRequest httpRequest;

    private final HttpStatusCode httpStatusCode;
    private Map<String, HashSet<String>> headers = null;
    private HashSet<Cookie> cookies = null;
    private ContentSecurityPolicy contentSecurityPolicy = null;
    private ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly = null;
    private StrictTransportSecurity strictTransportSecurity = null;
    private boolean xContentTypeOptionsNoSniff;
    private CrossOriginResourceSharing crossOriginResourceSharing = null;
    private HashSet<WWWAuthentication> wwwAuthentications = null;
    private Content content = null;

    public HttpResponse(final HttpRequest httpRequest,
                        final HttpStatusCode httpStatusCode) {
        if (httpRequest == null) {
            throw new NullPointerException("invalid http request passed");
        }
        this.httpRequest = httpRequest;
        this.httpStatusCode = httpStatusCode;
    }

    public HttpResponse header(final String name,
                               final HashSet<String> values) {
        if (aborted) return this;
        if (name == null || values == null || values.isEmpty()) {
            logger.atError().log("invalid header detected, " +
                    "aborting with " + HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
            httpRequest.getRequestConsumer().dropConnection(HttpStatusCode.INTERNAL_SERVER_ERROR);
            aborted = true;
            return this;
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, values);
        return this;
    }

    public HttpResponse cookie(final Cookie cookie) {
        if (aborted) return this;
        if (cookie == null) {
            logger.atDebug().log("cookie with null value detected, ignoring");
            return this;
        }
        if (cookies == null) {
            cookies = new HashSet<>();
        }
        cookies.add(cookie);
        return this;
    }

    public HttpResponse contentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        if (aborted) return this;
        if (contentSecurityPolicy == null) {
            logger.atDebug().log("content security policy with null value detected, ignoring");
            return this;
        }
        this.contentSecurityPolicy = contentSecurityPolicy;
        return this;
    }

    public HttpResponse contentSecurityPolicy(final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        if (aborted) return this;
        if (contentSecurityPolicyReportOnly == null) {
            logger.atDebug().log("content security policy report only with null value detected, ignoring");
            return this;
        }
        this.contentSecurityPolicyReportOnly = contentSecurityPolicyReportOnly;
        return this;
    }

    public HttpResponse strictTransportSecurity(final StrictTransportSecurity strictTransportSecurity) {
        if (aborted) return this;
        if (strictTransportSecurity == null) {
            logger.atDebug().log("strict transport security with null value detected, ignoring");
            return this;
        }
        this.strictTransportSecurity = strictTransportSecurity;
        return this;
    }

    public HttpResponse xContentTypeOptionsNoSniff(final boolean xContentTypeOptionsNoSniff) {
        if (aborted) return this;
        this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    public HttpResponse crossOriginResourceSharing(final CrossOriginResourceSharing crossOriginResourceSharing) {
        if (aborted) return this;
        if (crossOriginResourceSharing == null) {
            logger.atDebug().log("cross origin resource sharing with null value detected, ignoring");
            return this;
        }
        this.crossOriginResourceSharing = crossOriginResourceSharing;
        return this;
    }

    public HttpResponse wwwAuthentication(final WWWAuthentication wwwAuthentication) {
        if (aborted) return this;
        if (wwwAuthentication == null) {
            logger.atDebug().log("www authentication with null value detected, ignoring");
            return this;
        }
        if (wwwAuthentications == null) {
            wwwAuthentications = new HashSet<>();
        }
        wwwAuthentications.add(wwwAuthentication);
        return this;
    }

    public HttpResponse content(final Content content) {
        if (aborted) return this;
        if (content == null) {
            logger.atDebug().log("content with null value detected, ignoring");
            return this;
        }
        this.content = content;
        return this;
    }

    public boolean isAborted() {
        return aborted;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public Map<String, HashSet<String>> getHeaders() {
        return headers;
    }

    public HashSet<Cookie> getCookies() {
        return cookies;
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

    public HashSet<WWWAuthentication> getWWWAuthentications() {
        return wwwAuthentications;
    }

    public Content getContent() {
        return content;
    }
}