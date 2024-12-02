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
    private final Map<CSPDirective, HashSet<String>> policy = new HashMap<>();
    private Map<String, String> reportingEndpoint;

    public ContentSecurityPolicy(final CSPDirective directive,
                                 final HashSet<String> value) throws ContentSecurityPolicyException {
        if (directive == null || value == null || value.isEmpty()) {
            throw new ContentSecurityPolicyException("directive cannot be null");
        }
        policy.put(directive, value);
    }

    public ContentSecurityPolicy addPolicy(final CSPDirective directive,
                                           final HashSet<String> value) throws ContentSecurityPolicyException {
        if (directive == null || value == null || value.isEmpty()) {
            throw new ContentSecurityPolicyException("directive cannot be null");
        }
        policy.put(directive, value);
        return this;
    }

    public ContentSecurityPolicy addReportingEndpoint(final String name,
                                                      final String url) throws ContentSecurityPolicyException {
        if (name == null || url == null) {
            throw new ContentSecurityPolicyException("directive cannot be null");
        }
        if (reportingEndpoint == null) {
            reportingEndpoint = new HashMap<>();
        }
        reportingEndpoint.put(name, url);
        return this;
    }

    public Map<CSPDirective, HashSet<String>> getPolicy() {
        return policy;
    }

    public Map<String, String> getReportingEndpoint() {
        return reportingEndpoint;
    }

    public static String processOutgoingCsp(ContentSecurityPolicy contentSecurityPolicy,
                                            final ContentSecurityPolicyReportOnly contentSecurityPolicyReportOnly) {
        StringBuilder output = null;
        StringBuilder reportEndpoint = null;
        StringBuilder tempOutput = null;

        for (int j = 0; true; j++) {
            StringBuilder csp = new StringBuilder();

            if (j == 1) {
                contentSecurityPolicy = contentSecurityPolicyReportOnly;
            }

            if (contentSecurityPolicy != null) {
                csp.append("Content-Security-Policy").append(j == 1 ? "-Report-Only" : "").append(":");
                for (CSPDirective directive : contentSecurityPolicy.getPolicy().keySet()) {
                    csp.append(" ").append(directive.getName());

                    List<String> values = contentSecurityPolicy.getPolicy().get(directive).stream().toList();
                    for (int i = 0; i < values.size(); i++) {
                        csp.append(" ").append(values.get(i));

                        if (i != values.size() - 1) {
                            csp.append(";");
                        }
                    }
                }

                if (contentSecurityPolicy.getReportingEndpoint() != null) {
                    if (reportEndpoint == null) {
                        reportEndpoint = new StringBuilder().append("Reporting-Endpoints:").append(" ");
                    } else {
                        reportEndpoint.append(" ");
                    }

                    csp.append(";").append(" ").append("report-to");
                    List<String> keys = contentSecurityPolicy.getReportingEndpoint().keySet().stream().toList();
                    for (int i = 0; i < contentSecurityPolicy.getReportingEndpoint().size(); i++) {
                        csp.append(" ").append(keys.get(i));
                        reportEndpoint.append(" ").append(keys.get(i)).append("=").append("\"")
                                .append(contentSecurityPolicy.getReportingEndpoint().get(keys.get(i))).append("\"");

                        if (i != keys.size() - 1) {
                            reportEndpoint.append(",");
                        }
                    }
                }

                csp.append("\r\n");
            }

            if (j == 1) {
                if (reportEndpoint != null) {
                    output = new StringBuilder();
                    output.append(reportEndpoint).append("\r\n");
                }
                return output == null ? "" : output.append(tempOutput).append("\r\n")
                        .append(csp).append("\r\n").toString();
            }

            tempOutput = csp;
        }
    }
}
