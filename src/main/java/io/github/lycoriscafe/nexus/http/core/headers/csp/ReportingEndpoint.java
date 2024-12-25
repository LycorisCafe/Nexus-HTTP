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
 * Reporting Endpoint for CSP. Instance of ths class will process as a link to {@code REPORT_TO} CSP directive.
 * <pre>
 *     {@code
 *     // Example code
 *     var reportingEndpoint = new ReportingEndpoint("myEndpoint", "/sampleEndpoint");
 *     }
 *     {@code
 *     // Usage with ContentSecurityPolicy or ContentSecurityPolicyReportOnly
 *     var reportingEndpoint = new ReportingEndpoint("myEndpoint", "/sampleEndpoint");
 *     var csp = new ContentSecurityPolicy(REPORT_TO, "myEndpoint");
 *     }
 * </pre>
 *
 * @see ContentSecurityPolicy
 * @see ContentSecurityPolicyReportOnly
 * @see CSPDirective
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://www.w3.org/TR/CSP">Content Security Policy (w3c)</a>
 * @since v1.0.0
 */
public final class ReportingEndpoint {
    private final String name;
    private final String value;

    /**
     * Create instance of {@code ReportingEndpoint}.
     *
     * @param name  Endpoint name
     * @param value Endpoint value
     * @see ReportingEndpoint
     * @since v1.0.0
     */
    public ReportingEndpoint(final String name,
                             final String value) {
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Get provided endpoint name.
     *
     * @return Endpoint name
     * @see ReportingEndpoint
     * @since v1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Get provided endpoint value.
     *
     * @return Endpoint value
     * @see ReportingEndpoint
     * @since v1.0.0
     */
    public String getValue() {
        return value;
    }

    /**
     * Process provided {@code List} of {@code ReportingEndpoint} as an HTTP header, {@code Reporting-Endpoints}.
     *
     * @param reportingEndpoints {@code List} of {@code ReportingEndpoint}
     * @return Processed {@code Reporting-Endpoints} HTTP header string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see ReportingEndpoint
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
     * @since v1.0.0
     */
    public static String processOutgoingReportingEndpoints(final List<ReportingEndpoint> reportingEndpoints) {
        if (reportingEndpoints == null || reportingEndpoints.isEmpty()) return "";
        StringBuilder output = new StringBuilder().append("Reporting-Endpoints:");
        for (int i = 0; i < reportingEndpoints.size(); i++) {
            output.append(" ").append(reportingEndpoints.get(i).getName()).append("=").append("\"")
                    .append(reportingEndpoints.get(i).getValue()).append("\"");
            if (i != reportingEndpoints.size() - 1) output.append(",");
        }
        return output.toString();
    }
}
