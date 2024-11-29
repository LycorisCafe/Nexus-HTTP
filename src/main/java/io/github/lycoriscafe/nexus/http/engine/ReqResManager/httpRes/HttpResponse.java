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
import io.github.lycoriscafe.nexus.http.core.headers.cookies.Cookie;
import io.github.lycoriscafe.nexus.http.core.headers.cors.CrossOriginResourceSharing;
import io.github.lycoriscafe.nexus.http.core.headers.csp.CSPDirective;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicy;
import io.github.lycoriscafe.nexus.http.core.headers.csp.ContentSecurityPolicyReportOnly;
import io.github.lycoriscafe.nexus.http.core.headers.hsts.StrictTransportSecurity;
import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpResponse {
    private final long RESPONSE_ID;
    private final HttpStatusCode status;
    private final Map<String, List<String>> headers;
    private final List<Cookie> cookies;
    private final List<ContentSecurityPolicy> contentSecurityPolicies;
    private final List<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnlys;
    private final StrictTransportSecurity strictTransportSecurity;
    private final boolean xContentTypeOptionsNoSniff;
    private final CrossOriginResourceSharing crossOriginResourceSharing;
    private final List<WWWAuthentication> wwwAuthentications;
    private final Object content;

    private HttpResponse(HttpResponseBuilder builder) {
        this.RESPONSE_ID = builder.RESPONSE_ID;
        this.status = builder.status;
        this.headers = builder.headers;
        this.cookies = builder.cookies;
        this.contentSecurityPolicies = builder.contentSecurityPolicies;
        this.contentSecurityPolicyReportOnlys = builder.contentSecurityPolicyReportOnlys;
        this.strictTransportSecurity = builder.strictTransportSecurity;
        this.xContentTypeOptionsNoSniff = builder.xContentTypeOptionsNoSniff;
        this.crossOriginResourceSharing = builder.crossOriginResourceSharing;
        this.wwwAuthentications = builder.wwwAuthentications;
        this.content = builder.content;
    }

    public long getResponseId() {
        return RESPONSE_ID;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public List<ContentSecurityPolicy> getContentSecurityPolicies() {
        return contentSecurityPolicies;
    }

    public List<ContentSecurityPolicyReportOnly> getContentSecurityPolicyReportOnlys() {
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

    public List<WWWAuthentication> getWWWAuthentications() {
        return wwwAuthentications;
    }

    public Object getContent() {
        return content;
    }

    public static HttpResponseBuilder builder(long RESPONSE_ID) {
        return new HttpResponseBuilder(RESPONSE_ID);
    }

    public static class HttpResponseBuilder {
        private boolean csproReport = false;

        private final long RESPONSE_ID;
        private HttpStatusCode status;
        private Map<String, List<String>> headers;
        private List<Cookie> cookies;
        private List<ContentSecurityPolicy> contentSecurityPolicies;
        private List<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnlys;
        private StrictTransportSecurity strictTransportSecurity;
        private boolean xContentTypeOptionsNoSniff;
        private CrossOriginResourceSharing crossOriginResourceSharing;
        private List<WWWAuthentication> wwwAuthentications;
        private Object content;

        public HttpResponseBuilder(long RESPONSE_ID) {
            this.RESPONSE_ID = RESPONSE_ID;
        }

        public HttpResponseBuilder status(HttpStatusCode status)
                throws HttpResponseException {
            if (status == null) {
                throw new HttpResponseException("http status code cannot be null");
            }
            this.status = status;
            return this;
        }

        public HttpResponseBuilder header(String name,
                                          List<String> values)
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

        public HttpResponseBuilder cookie(Cookie cookie)
                throws HttpResponseException {
            if (cookie == null) {
                throw new HttpResponseException("cookie cannot be null");
            }
            if (cookies == null) {
                cookies = new ArrayList<>();
            }
            cookies.add(cookie);
            return this;
        }

        public HttpResponseBuilder contentSecurityPolicy(ContentSecurityPolicy contentSecurityPolicy)
                throws HttpResponseException {
            if (contentSecurityPolicy == null) {
                throw new HttpResponseException("content-security-policy cannot be null");
            }
            if (contentSecurityPolicies == null) {
                contentSecurityPolicies = new ArrayList<>();
            }
            contentSecurityPolicies.add(contentSecurityPolicy);
            return this;
        }

        public HttpResponseBuilder contentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly)
                throws HttpResponseException {
            if (contentSecurityPolicyReportOnly == null) {
                throw new HttpResponseException("content-security-policy-report-only cannot be null");
            }
            if (contentSecurityPolicyReportOnlys == null) {
                contentSecurityPolicyReportOnlys = new ArrayList<>();
            }
            contentSecurityPolicyReportOnlys.add(contentSecurityPolicyReportOnly);
            if (contentSecurityPolicyReportOnly.getDirective() == CSPDirective.REPORT_TO ||
                    contentSecurityPolicyReportOnly.getDirective() == CSPDirective.REPORT_URI) {
                csproReport = true;
            }
            return this;
        }

        public HttpResponseBuilder strictTransportSecurity(StrictTransportSecurity strictTransportSecurity)
                throws HttpResponseException {
            if (strictTransportSecurity == null) {
                throw new HttpResponseException("strict-transport-security cannot be null");
            }
            this.strictTransportSecurity = strictTransportSecurity;
            return this;
        }

        public HttpResponseBuilder xContentTypeOptionsNoSniff(boolean xContentTypeOptionsNoSniff) {
            this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
            return this;
        }

        public HttpResponseBuilder crossOriginResourceSharing(CrossOriginResourceSharing crossOriginResourceSharing)
                throws HttpResponseException {
            if (crossOriginResourceSharing == null) {
                throw new HttpResponseException("cross origin resource sharing cannot be null");
            }
            this.crossOriginResourceSharing = crossOriginResourceSharing;
            return this;
        }

        public HttpResponseBuilder wwwAuthentication(WWWAuthentication wwwAuthentication)
                throws HttpResponseException {
            if (wwwAuthentication == null) {
                throw new HttpResponseException("www authentication cannot be null");
            }
            if (wwwAuthentications == null) {
                wwwAuthentications = new ArrayList<>();
            }
            wwwAuthentications.add(wwwAuthentication);
            return this;
        }

        public HttpResponseBuilder content(Object content)
                throws IllegalArgumentException {
            if (!(content instanceof byte[] || content instanceof File)) {
                throw new IllegalArgumentException("Content must be a byte array or a file. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.content = content;
            return this;
        }

        public HttpResponse build() throws HttpResponseException {
            if (!contentSecurityPolicyReportOnlys.isEmpty() && !csproReport) {
                throw new HttpResponseException("ContentSecurityPolicyReportOnly requires a " +
                        "report-to/report-uri to be specified");
            }
            return new HttpResponse(this);
        }
    }
}