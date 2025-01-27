/*
 * Copyright 2025 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes;

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
import io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq.HttpRequest;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;
import io.github.lycoriscafe.nexus.http.helper.configuration.HttpServerConfiguration;
import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * HTTP response class.
 *
 * @see HttpRequest
 * @since v1.0.0
 */
public final class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

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

    /**
     * Create instance of {@code HttpResponse}
     *
     * @param requestId       {@code HttpRequest} id
     * @param requestConsumer {@code RequestConsumer} bound to the {@code HttpRequest}
     * @see HttpResponse
     * @see HttpRequest
     * @see HttpRequest#getRequestId()
     * @see RequestConsumer
     * @since v1.0.0
     */
    public HttpResponse(final long requestId,
                        final RequestConsumer requestConsumer) {
        if (requestId < 0) throw new IllegalStateException("invalid response id passed");
        this.requestId = requestId;
        this.requestConsumer = Objects.requireNonNull(requestConsumer);

        headers = requestConsumer.getHttpServerConfiguration().getDefaultHeaders();
        cookies = requestConsumer.getHttpServerConfiguration().getDefaultCookies();
        contentSecurityPolicies = requestConsumer.getHttpServerConfiguration().getDefaultContentSecurityPolicies();
        contentSecurityPolicyReportOnly = requestConsumer.getHttpServerConfiguration().getDefaultContentSecurityPolicyReportOnly();
        strictTransportSecurity = requestConsumer.getHttpServerConfiguration().getDefaultStrictTransportSecurity();
        xContentTypeOptionsNoSniff = requestConsumer.getHttpServerConfiguration().isDefaultXContentTypeOptionsNoSniff();
        corsResponse = requestConsumer.getHttpServerConfiguration().getDefaultCors();
        cacheControl = requestConsumer.getHttpServerConfiguration().getDefaultCacheControl();
    }

    /**
     * Get provided request id.
     *
     * @return Request id
     * @see HttpResponse
     * @since v1.0.0
     */
    public long getRequestId() {
        return requestId;
    }

    /**
     * Get provided {@code RequestConsumer}.
     *
     * @return {@code RequestConsumer}
     * @see RequestConsumer
     * @see HttpResponse
     * @since v1.0.0
     */
    public RequestConsumer getRequestConsumer() {
        return requestConsumer;
    }

    /**
     * Set the HTTP status code for this response. If you don't set this, the default status will {@code 200 OK}.
     *
     * @param httpStatusCode HTTP status code
     * @return Same {@code HttpResponse} instance
     * @see HttpStatusCode
     * @see HttpResponse
     * @since v1.0.0
     */
    public HttpResponse setStatusCode(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = Objects.requireNonNull(httpStatusCode);
        return this;
    }

    /**
     * Get HTTP status code for this response.
     *
     * @return HTTP status code
     * @see HttpStatusCode
     * @see HttpResponse#setStatusCode(HttpStatusCode)
     * @see HttpResponse
     * @since v1.0.0
     */
    public HttpStatusCode getStatusCode() {
        return httpStatusCode;
    }

    /**
     * Set non-specific headers one-by-one.
     *
     * @param header {@code Header}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default non-specific headers specified in the {@code HttpServerConfiguration}
     * for this response.
     * @see Header
     * @see HttpResponse#setHeaders(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultHeader(Header)
     * @see HttpServerConfiguration#setDefaultHeaders(List)
     * @since v1.0.0
     */
    public HttpResponse addHeader(final Header header) {
        if (header == null) {
            headers = null;
            return this;
        }

        if (headers == null) headers = new NonDuplicateList<>();
        headers.add(header);
        return this;
    }

    /**
     * Set non-specific headers.
     *
     * @param headers {@code List} of {@code Header}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default non-specific headers specified in the {@code HttpServerConfiguration}
     * for this response.
     * @see Header
     * @see HttpResponse#setHeaders(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultHeader(Header)
     * @see HttpServerConfiguration#setDefaultHeaders(List)
     * @since v1.0.0
     */
    public HttpResponse setHeaders(final List<Header> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Get non-specific headers.
     *
     * @return {@code List} of {@code Header}
     * @see Header
     * @see HttpResponse#addHeader(Header)
     * @see HttpResponse#setHeaders(List)
     * @see HttpResponse
     * @since v1.0.0
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * Set cookie one-by-one.
     *
     * @param cookie {@code Cookie}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default cookies specified in the {@code HttpServerConfiguration} for this
     * response.
     * @see Cookie
     * @see HttpResponse#setCookies(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultCookie(Cookie)
     * @see HttpServerConfiguration#setDefaultCookies(List)
     * @since v1.0.0
     */
    public HttpResponse addCookie(final Cookie cookie) {
        if (cookie == null) {
            this.cookies = null;
            return this;
        }

        if (cookies == null) cookies = new NonDuplicateList<>();
        cookies.add(cookie);
        return this;
    }

    /**
     * Set cookies.
     *
     * @param cookies {@code List} of {@code Cookie}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default cookies specified in the {@code HttpServerConfiguration} for this
     * response.
     * @see Cookie
     * @see HttpResponse#addCookie(Cookie)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultCookie(Cookie)
     * @see HttpServerConfiguration#setDefaultCookies(List)
     * @since v1.0.0
     */
    public HttpResponse setCookies(final List<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    /**
     * Get cookies.
     *
     * @return {@code List} of {@code Cookie}
     * @see Cookie
     * @see HttpResponse#addCookie(Cookie)
     * @see HttpResponse#setCookies(List)
     * @see HttpResponse
     * @since v1.0.0
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * Set reporting endpoints one-by-one.
     *
     * @param reportingEndpoint {@code ReportingEndpoint}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default reporting endpoints specified in the {@code HttpServerConfiguration}
     * for this response.
     * @see ReportingEndpoint
     * @see HttpResponse#setReportingEndpoints(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultReportingEndpoint(ReportingEndpoint)
     * @see HttpServerConfiguration#setDefaultReportingEndpoints(List)
     * @since v1.0.0
     */
    public HttpResponse addReportingEndpoint(final ReportingEndpoint reportingEndpoint) {
        if (reportingEndpoint == null) {
            reportingEndpoints = null;
            return this;
        }

        if (reportingEndpoints == null) reportingEndpoints = new NonDuplicateList<>();
        reportingEndpoints.add(reportingEndpoint);
        return this;
    }

    /**
     * Set reporting endpoints.
     *
     * @param reportingEndpoints {@code List} of {@code ReportingEndpoint}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default reporting endpoints specified in the {@code HttpServerConfiguration}
     * for this response.
     * @see ReportingEndpoint
     * @see HttpResponse#addReportingEndpoint(ReportingEndpoint)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultReportingEndpoint(ReportingEndpoint)
     * @see HttpServerConfiguration#setDefaultReportingEndpoints(List)
     * @since v1.0.0
     */
    public HttpResponse setReportingEndpoints(final List<ReportingEndpoint> reportingEndpoints) {
        this.reportingEndpoints = reportingEndpoints;
        return this;
    }

    /**
     * Get reporting endpoints.
     *
     * @return {@code List} of {@code ReportingEndpoint}
     * @see ReportingEndpoint
     * @see HttpResponse#addReportingEndpoint(ReportingEndpoint)
     * @see HttpResponse#setReportingEndpoints(List)
     * @see HttpResponse
     * @since v1.0.0
     */
    public List<ReportingEndpoint> getReportingEndpoints() {
        return reportingEndpoints;
    }

    /**
     * Set content security policies one-by-one.
     *
     * @param contentSecurityPolicy {@code ContentSecurityPolicy}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default content security policies specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see ContentSecurityPolicy
     * @see HttpResponse#setContentSecurityPolicies(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultContentSecurityPolicy(ContentSecurityPolicy)
     * @see HttpServerConfiguration#setDefaultContentSecurityPolicies(List)
     * @since v1.0.0
     */
    public HttpResponse addContentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        if (contentSecurityPolicy == null) {
            contentSecurityPolicies = null;
            return this;
        }

        if (contentSecurityPolicies == null) contentSecurityPolicies = new NonDuplicateList<>();
        contentSecurityPolicies.add(contentSecurityPolicy);
        return this;
    }

    /**
     * Set content security policies.
     *
     * @param contentSecurityPolicies {@code List} of {@code ContentSecurityPolicy}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default content security policies specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see ContentSecurityPolicy
     * @see HttpResponse#addContentSecurityPolicy(ContentSecurityPolicy)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultContentSecurityPolicy(ContentSecurityPolicy)
     * @see HttpServerConfiguration#setDefaultContentSecurityPolicies(List)
     * @since v1.0.0
     */
    public HttpResponse setContentSecurityPolicies(final List<ContentSecurityPolicy> contentSecurityPolicies) {
        this.contentSecurityPolicies = contentSecurityPolicies;
        return this;
    }

    /**
     * Get content security policies.
     *
     * @return {@code List} of {@code ContentSecurityPolicy}
     * @see ReportingEndpoint
     * @see HttpResponse#addContentSecurityPolicy(ContentSecurityPolicy)
     * @see HttpResponse#setContentSecurityPolicies(List)
     * @see HttpResponse
     * @since v1.0.0
     */
    public List<ContentSecurityPolicy> getContentSecurityPolicies() {
        return contentSecurityPolicies;
    }

    /**
     * Set content security policy report only(s) one-by-one.
     *
     * @param contentSecurityPolicyReportOnly {@code ContentSecurityPolicyReportOnly}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default content security policy report only(s) specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see ContentSecurityPolicyReportOnly
     * @see HttpResponse#setContentSecurityPolicyReportOnly(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see HttpServerConfiguration#setDefaultContentSecurityPolicyReportOnly(List)
     * @since v1.0.0
     */
    public HttpResponse addContentSecurityPolicyReportOnly(final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        if (contentSecurityPolicyReportOnly == null) {
            this.contentSecurityPolicyReportOnly = null;
            return this;
        }

        if (this.contentSecurityPolicyReportOnly == null)
            this.contentSecurityPolicyReportOnly = new NonDuplicateList<>();
        this.contentSecurityPolicyReportOnly.add(contentSecurityPolicyReportOnly);
        return this;
    }

    /**
     * Set content security policy report only(s).
     *
     * @param contentSecurityPolicyReportOnly {@code List} of {@code ContentSecurityPolicyReportOnly}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default content security policy report only(s) specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see ContentSecurityPolicyReportOnly
     * @see HttpResponse#addContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see HttpServerConfiguration#setDefaultContentSecurityPolicyReportOnly(List)
     * @since v1.0.0
     */
    public HttpResponse setContentSecurityPolicyReportOnly(final List<ContentSecurityPolicyReportOnly> contentSecurityPolicyReportOnly) {
        this.contentSecurityPolicyReportOnly = contentSecurityPolicyReportOnly;
        return this;
    }

    /**
     * Get the content security policy report only(s).
     *
     * @return {@code List} of {@code ContentSecurityPolicyReportOnly}
     * @see ReportingEndpoint
     * @see HttpResponse#addContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnly)
     * @see HttpResponse#setContentSecurityPolicyReportOnly(List)
     * @see HttpResponse
     * @since v1.0.0
     */
    public List<ContentSecurityPolicyReportOnly> getContentSecurityPolicyReportOnly() {
        return contentSecurityPolicyReportOnly;
    }

    /**
     * Set strict transport security (HSTS).
     *
     * @param strictTransportSecurity {@code StrictTransportSecurity}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default strict transport security specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see StrictTransportSecurity
     * @see HttpResponse
     * @see HttpServerConfiguration#setDefaultStrictTransportSecurity(StrictTransportSecurity)
     * @since v1.0.0
     */
    public HttpResponse setStrictTransportSecurity(final StrictTransportSecurity strictTransportSecurity) {
        this.strictTransportSecurity = strictTransportSecurity;
        return this;
    }

    /**
     * Get strict transport security.
     *
     * @return {@code StrictTransportSecurity}
     * @see StrictTransportSecurity
     * @see HttpResponse#setStrictTransportSecurity(StrictTransportSecurity)
     * @see HttpResponse
     * @since v1.0.0
     */
    public StrictTransportSecurity getStrictTransportSecurity() {
        return strictTransportSecurity;
    }

    /**
     * Set {@code X-Content-Type-Options: nosniff} status.
     *
     * @param xContentTypeOptionsNoSniff {@code X-Content-Type-Options: nosniff} status
     * @return Same {@code HttpResponse} instance
     * @see HttpResponse
     * @see HttpServerConfiguration#setDefaultXContentTypeNoSniff(boolean)
     * @since v1.0.0
     */
    public HttpResponse setXContentTypeOptionsNoSniff(final boolean xContentTypeOptionsNoSniff) {
        this.xContentTypeOptionsNoSniff = xContentTypeOptionsNoSniff;
        return this;
    }

    /**
     * Get {@code X-Content-Type-Options: nosniff} status.
     *
     * @return {@code X-Content-Type-Options: nosniff} status
     * @see HttpResponse#setXContentTypeOptionsNoSniff(boolean)
     * @see HttpResponse
     * @since v1.0.0
     */
    public boolean isXContentTypeOptionsNoSniff() {
        return xContentTypeOptionsNoSniff;
    }

    /**
     * Set cross-origin resource sharing headers.
     *
     * @param corsResponse {@code CORSResponse}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default cross-origin resource sharing specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see CORSResponse
     * @see HttpResponse
     * @see HttpServerConfiguration#setDefaultCors(CORSResponse)
     * @since v1.0.0
     */
    public HttpResponse setCorsResponse(final CORSResponse corsResponse) {
        this.corsResponse = corsResponse;
        return this;
    }

    /**
     * Get cross-origin resource sharing headers.
     *
     * @return {@code CORSResponse}
     * @see CORSResponse
     * @see HttpResponse
     * @since v1.0.0
     */
    public CORSResponse getCorsResponse() {
        return corsResponse;
    }

    /**
     * Set authentication challenges one-by-one.
     *
     * @param authentication {@code Authentication}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default authentication challenges specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see Authentication
     * @see HttpResponse#setAuthentications(List)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultAuthentication(Authentication)
     * @see HttpServerConfiguration#setDefaultAuthentications(List)
     * @since v1.0.0
     */
    public HttpResponse addAuthentication(final Authentication authentication) {
        if (authentication == null) {
            authentications = null;
            return this;
        }

        if (authentications == null) authentications = new NonDuplicateList<>();
        authentications.add(authentication);
        return this;
    }

    /**
     * Set authentication challenges.
     *
     * @param authentications {@code List} of {@code Authentication}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default authentication challenges specified in the
     * {@code HttpServerConfiguration} for this response.
     * @see Authentication
     * @see HttpResponse#addAuthentication(Authentication)
     * @see HttpResponse
     * @see HttpServerConfiguration#addDefaultAuthentication(Authentication)
     * @see HttpServerConfiguration#setDefaultAuthentications(List)
     * @since v1.0.0
     */
    public HttpResponse setAuthentications(final List<Authentication> authentications) {
        this.authentications = authentications;
        return this;
    }

    /**
     * Get authentication challenges.
     *
     * @return {@code List} of {@code Authentication}
     * @see Authentication
     * @see HttpResponse#addAuthentication(Authentication)
     * @see HttpResponse#setAuthentications(List)
     * @see HttpResponse
     * @since v1.0.0
     */
    public List<Authentication> getAuthentications() {
        return authentications;
    }

    /**
     * Set cache control headers.
     *
     * @param cacheControl {@code CacheControl}
     * @return Same {@code HttpResponse} instance
     * @apiNote By passing {@code null} value, API users can reset the default cache control specified in the {@code HttpServerConfiguration} for this
     * response.
     * @see CacheControl
     * @see HttpResponse
     * @see HttpServerConfiguration#setDefaultCacheControl(CacheControl)
     * @since v1.0.0
     */
    public HttpResponse setCashControl(final CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    /**
     * Get cache control headers.
     *
     * @return {@code CacheControl}
     * @see CacheControl
     * @see HttpResponse
     * @since v1.0.0
     */
    public CacheControl getCacheControl() {
        return cacheControl;
    }

    /**
     * Set content.
     *
     * @param content {@code Content}
     * @return Same {@code HttpResponse} instance
     * @see Content
     * @see HttpResponse
     * @since v1.0.0
     */
    public HttpResponse setContent(final Content content) {
        this.content = content;
        return this;
    }

    /**
     * Get content.
     *
     * @return {@code Content}
     * @see Content
     * @see HttpResponse
     * @since v1.0.0
     */
    public Content getContent() {
        return content;
    }

    /**
     * Mark socket should drop the connection after sending this response.
     *
     * @param dropConnection Should drop?
     * @return Same {@code HttpResponse} instance
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see RequestConsumer#send(HttpResponse)
     * @see HttpResponse
     * @since v1.0.0
     */
    public HttpResponse setDropConnection(final boolean dropConnection) {
        this.dropConnection = dropConnection;
        return this;
    }

    /**
     * Get is this response marked as drop connection response?
     *
     * @return Is the drop marked?
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see RequestConsumer#send(HttpResponse)
     * @see HttpResponse
     * @since v1.0.0
     */
    public boolean isDropConnection() {
        return dropConnection;
    }

    /**
     * Finalize the response. It means assembling status code, header fields and process content-related operations.
     *
     * @return Processed HTTP headers string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see HttpResponse
     * @see HttpRequest#finalizeRequest()
     * @since v1.0.0
     */
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
                    .append(Content.WriteOperations.processOutgoingContent(getRequestConsumer().getHttpServerConfiguration(), getContent()));
            if (isXContentTypeOptionsNoSniff()) output.append("X-Content-Type-Options: nosniff").append("\r\n");
            return output.append("\r\n").toString();
        } catch (Exception e) {
            requestConsumer.dropConnection(getRequestId(), HttpStatusCode.INTERNAL_SERVER_ERROR, "error while parsing http response", logger);
            return null;
        }
    }
}