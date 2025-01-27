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

package io.github.lycoriscafe.nexus.http.core.headers.csp;

import java.util.List;

/**
 * Content Security Policy Report Only (CSP - Report Only) for HTTP responses.
 * <pre>
 *     {@code
 *     // Example code
 *     var csp = new ContentSecurityPolicyReportOnly(IMG_SRC, List.of("sampleData"));
 *     }
 * </pre>
 *
 * @apiNote When using {@code REPORT_TO} directive, you should specify it by passing an instance of {@code ReportingEndpoint} to the
 * {@code HttpResponse}.
 * @see ReportingEndpoint
 * @see io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://www.w3.org/TR/CSP">Content Security Policy (w3c)</a>
 * @since v1.0.0
 */
public final class ContentSecurityPolicyReportOnly extends ContentSecurityPolicy {
    /**
     * Create instance of {@code ContentSecurityPolicyReportOnly}.
     *
     * @param directive CSP directive
     * @param values    {@code List} of CSP directive values
     * @see CSPDirective
     * @see ContentSecurityPolicy
     * @see ContentSecurityPolicyReportOnly
     * @since v1.0.0
     */
    public ContentSecurityPolicyReportOnly(final CSPDirective directive,
                                           final List<String> values) {
        super(directive, values);
    }
}
