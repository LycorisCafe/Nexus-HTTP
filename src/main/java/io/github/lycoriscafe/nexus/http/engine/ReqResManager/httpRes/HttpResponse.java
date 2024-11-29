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
import io.github.lycoriscafe.nexus.http.core.headers.csp.CSPDirective;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class HttpResponse {
    private boolean csproReport;

    private final long RESPONSE_ID;
    private HttpStatusCode status;
    private Map<String, HashSet<String>> headers;
    private HashSet<Cookie> cookies;
    private HashSet<ContentSecurityPolicy> contentSecurityPolicies;
    private HashSet<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnlys;
    private StrictTransportSecurity strictTransportSecurity;
    private boolean xContentTypeOptionsNoSniff;
    private CrossOriginResourceSharing crossOriginResourceSharing;
    private HashSet<WWWAuthentication> wwwAuthentications;
    private Content content;

    public HttpResponse(long RESPONSE_ID) {
        this.RESPONSE_ID = RESPONSE_ID;
    }

    public HttpResponse status(HttpStatusCode status)
            throws HttpResponseException {
        if (status == null) {
            throw new HttpResponseException("http status code cannot be null");
        }
        this.status = status;
        return this;
    }

    public HttpResponse header(String name,
                               HashSet<String> values)
            throws HttpResponseException {
        if (name == null || values == null) {
            throw new HttpResponseException("parameters cannot be null");
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, values);
        return this;
    }

    public HttpResponse cookie(Cookie cookie)
            throws HttpResponseException {
        if (cookie == null) {
            throw new HttpResponseException("cookie cannot be null");
        }
        if (cookies == null) {
            cookies = new HashSet<>();
        }
        cookies.add(cookie);
        return this;
    }

    public HttpResponse contentSecurityPolicy(ContentSecurityPolicy contentSecurityPolicy)
            throws HttpResponseException {
        if (contentSecurityPolicy == null) {
            throw new HttpResponseException("content-security-policy cannot be null");
        }
        if (contentSecurityPolicies == null) {
            contentSecurityPolicies = new HashSet<>();
        }
        contentSecurityPolicies.add(contentSecurityPolicy);
        return this;
    }

    public HttpResponse contentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly)
            throws HttpResponseException {
        if (contentSecurityPolicyReportOnly == null) {
            throw new HttpResponseException("content-security-policy-report-only cannot be null");
        }
        if (contentSecurityPolicyReportOnlys == null) {
            contentSecurityPolicyReportOnlys = new HashSet<>();
        }
        contentSecurityPolicyReportOnlys.add(contentSecurityPolicyReportOnly);
        if (contentSecurityPolicyReportOnly.getDirective() == CSPDirective.REPORT_TO ||
                contentSecurityPolicyReportOnly.getDirective() == CSPDirective.REPORT_URI) {
            csproReport = true;
        }
        return this;
    }

    public HttpResponse strictTransportSecurity(StrictTransportSecurity strictTransportSecurity)
            throws HttpResponseException {
        if (strictTransportSecurity == null) {
            throw new HttpResponseException("strict-transport-security cannot be null");
        }
        this.strictTransportSecurity = strictTransportSecurity;
        return this;
    }

    public HttpResponse xContentTypeOptionsNoSniff(boolean xContentTypeOptionsNoSniff) {
        this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    public HttpResponse crossOriginResourceSharing(CrossOriginResourceSharing crossOriginResourceSharing)
            throws HttpResponseException {
        if (crossOriginResourceSharing == null) {
            throw new HttpResponseException("cross origin resource sharing cannot be null");
        }
        this.crossOriginResourceSharing = crossOriginResourceSharing;
        return this;
    }

    public HttpResponse wwwAuthentication(WWWAuthentication wwwAuthentication)
            throws HttpResponseException {
        if (wwwAuthentication == null) {
            throw new HttpResponseException("www authentication cannot be null");
        }
        if (wwwAuthentications == null) {
            wwwAuthentications = new HashSet<>();
        }
        wwwAuthentications.add(wwwAuthentication);
        return this;
    }

    public HttpResponse content(Content content)
            throws IllegalArgumentException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.content = content;
        return this;
    }

    public long getResponseId() {
        return RESPONSE_ID;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public Map<String, HashSet<String>> getHeaders() {
        return headers;
    }

    public HashSet<Cookie> getCookies() {
        return cookies;
    }

    public HashSet<ContentSecurityPolicy> getContentSecurityPolicies() {
        return contentSecurityPolicies;
    }

    public HashSet<ContentSecurityPolicyReportOnly> getContentSecurityPolicyReportOnlys() {
        return contentSecurityPolicyReportOnlys;
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