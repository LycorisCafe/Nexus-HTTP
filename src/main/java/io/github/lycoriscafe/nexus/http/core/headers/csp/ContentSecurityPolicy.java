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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public sealed class ContentSecurityPolicy permits ContentSecurityPolicyReportOnly {
    private final Map<CSPDirective, HashSet<String>> policies;
    private Map<String, String> reportingEndpoint;

    public ContentSecurityPolicy() {
        policies = new HashMap<>();
    }

    public ContentSecurityPolicy addPolicy(final CSPDirective directive,
                                           final String[] values) throws ContentSecurityPolicyException {
        if (directive == null || values == null || values.length < 1) {
            throw new ContentSecurityPolicyException("directive/values cannot be null");
        }
        policies.put(directive, new HashSet<>(List.of(values)));
        return this;
    }

    public Map<CSPDirective, HashSet<String>> getPolicies() {
        return policies;
    }

    public ContentSecurityPolicy addReportingEndpoint(final String name,
                                                      final String url) throws ContentSecurityPolicyException {
        if (name == null || url == null) throw new ContentSecurityPolicyException("directive cannot be null");
        if (reportingEndpoint == null) reportingEndpoint = new HashMap<>();
        reportingEndpoint.put(name, url);
        return this;
    }

    public Map<String, String> getReportingEndpoints() {
        return reportingEndpoint;
    }

    public static String processOutgoingCsp(final ContentSecurityPolicy contentSecurityPolicy,
                                            final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        StringBuilder output = null;
        StringBuilder reportingEndpoints = null;

        for (int i = 0; i < 2; i++) {
            ContentSecurityPolicy csp = switch (i) {
                case 0 -> contentSecurityPolicy;
                case 1 -> contentSecurityPolicyReportOnly;
                default -> null;
            };

            if (csp == null) continue;
            StringBuilder tempBuilder = new StringBuilder().append("Content-Security-Policy")
                    .append(i == 0 ? "" : "-Report-Only").append(":").append(" ");
            for (CSPDirective key : csp.getPolicies().keySet()) {
                tempBuilder.append(key).append(" ");
                for (String value : csp.getPolicies().get(key)) {
                    tempBuilder.append(value).append(" ");
                }
                tempBuilder.append(";");
            }
            tempBuilder.append("\r\n");

            if (output == null) output = new StringBuilder();
            output.append(tempBuilder);

            if (csp.getReportingEndpoints() == null) continue;
            if (reportingEndpoints == null) reportingEndpoints = new StringBuilder().append("Reporting-Endpoints:");
            for (String key : csp.getReportingEndpoints().keySet()) {
                reportingEndpoints.append(" ").append(key).append("=").append("\"")
                        .append(csp.getReportingEndpoints().get(key)).append("\"");
            }
        }

        if (output == null) return "";
        if (reportingEndpoints != null) reportingEndpoints.append("\r\n");

        return (reportingEndpoints == null ? "" : reportingEndpoints.toString()) + output;
    }
}
