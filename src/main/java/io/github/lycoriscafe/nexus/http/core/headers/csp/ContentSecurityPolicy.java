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

package io.github.lycoriscafe.nexus.http.core.headers.csp;

import java.util.List;
import java.util.Objects;

/**
 * Content Security Policy (CSP) for HTTP responses.
 * <pre>
 *     {@code
 *     // Example code
 *     var csp = new ContentSecurityPolicy(IMG_SRC, List.of("sampleData"));
 *     }
 * </pre>
 *
 * @apiNote When using {@code REPORT_TO} directive, you should specify it by passing an instance of {@code ReportingEndpoint} to the
 * {@code HttpResponse}.
 * @see ReportingEndpoint
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://www.w3.org/TR/CSP">Content Security Policy (w3c)</a>
 * @since v1.0.0
 */
public sealed class ContentSecurityPolicy permits ContentSecurityPolicyReportOnly {
    private final CSPDirective directive;
    private final List<String> values;

    /**
     * Create instance of {@code ContentSecurityPolicy}.
     *
     * @param directive CSP directive
     * @param values    {@code List} of CSP directive values
     * @see CSPDirective
     * @see ContentSecurityPolicy
     * @since v1.0.0
     */
    public ContentSecurityPolicy(final CSPDirective directive,
                                 final List<String> values) {
        this.directive = Objects.requireNonNull(directive);
        this.values = Objects.requireNonNull(values);
        if (values.isEmpty()) throw new IllegalArgumentException("Values cannot be empty");
    }

    /**
     * Get provided CSP directive.
     *
     * @return CSP directive
     * @see CSPDirective
     * @see ContentSecurityPolicy
     * @since v1.0.0
     */
    public CSPDirective getDirective() {
        return directive;
    }

    /**
     * Get provided values for the CSP directive.
     *
     * @return {@code List} of CSP directive values
     * @see CSPDirective
     * @see ContentSecurityPolicy
     * @since v1.0.0
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Process {@code Content-Security-Policy} header from provided {@code List} of {@code ContentSecurityPolicy} or
     * {@code ContentSecurityPolicyReportOnly} instances.
     *
     * @param contentSecurityPolicies {@code List} of {@code ContentSecurityPolicy} or {@code ContentSecurityPolicyReportOnly} instances
     * @param reportOnly              Are the instances {@code ContentSecurityPolicyReportOnly}?
     * @return Processed {@code Content-Security-Policy} HTTP header.
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see ContentSecurityPolicy
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
     * @since v1.0.0
     */
    public static String processOutgoingCsp(final List<? extends ContentSecurityPolicy> contentSecurityPolicies,
                                            final boolean reportOnly) {
        if (contentSecurityPolicies == null || contentSecurityPolicies.isEmpty()) return "";
        StringBuilder output = new StringBuilder()
                .append(reportOnly ? "Content-SecurityPolicy-Report-Only" : "Content-SecurityPolicy").append(":");
        for (ContentSecurityPolicy contentSecurityPolicy : contentSecurityPolicies) {
            output.append(" ").append(contentSecurityPolicy.getDirective().name());
            for (String value : contentSecurityPolicy.getValues()) {
                output.append(" ").append(value);
            }
            output.append(";");
        }
        return output.append("\r\n").toString();
    }
}
