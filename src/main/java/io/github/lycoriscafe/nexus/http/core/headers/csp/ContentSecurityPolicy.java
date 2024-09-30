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

import java.util.ArrayList;
import java.util.List;

public sealed class ContentSecurityPolicy
        permits ContentSecurityPolicyReportOnly {
    private final CSPDirective directive;
    private final List<CSPValue> values;
    private final List<Object> hosts;

    ContentSecurityPolicy(ContentSecurityPolicyBuilder builder) {
        directive = builder.directive;
        values = builder.values;
        hosts = builder.hosts;
    }

    public CSPDirective getDirective() {
        return directive;
    }

    public List<CSPValue> getValues() {
        return values;
    }

    public List<Object> getHosts() {
        return hosts;
    }

    public static ContentSecurityPolicyBuilder builder(CSPDirective directive)
            throws ContentSecurityPolicyException {
        return new ContentSecurityPolicyBuilder(directive);
    }

    public static sealed class ContentSecurityPolicyBuilder
            permits ContentSecurityPolicyReportOnly.ContentSecurityPolicyReportOnlyBuilder {
        final CSPDirective directive;
        private final List<CSPValue> values;
        final List<Object> hosts;

        public ContentSecurityPolicyBuilder(CSPDirective directive)
                throws ContentSecurityPolicyException {
            if (directive == null) {
                throw new ContentSecurityPolicyException("directive cannot be null");
            }
            this.directive = directive;
            values = new ArrayList<>();
            hosts = new ArrayList<>();
        }

        public ContentSecurityPolicyBuilder value(CSPValue value)
                throws ContentSecurityPolicyException {
            if (directive == CSPDirective.REPORT_TO || directive == CSPDirective.REPORT_URI) {
                throw new ContentSecurityPolicyException("report-to & report-uri cannot be contain any value");
            }
            this.values.add(value);
            return this;
        }

        public ContentSecurityPolicyBuilder host(String host)
                throws ContentSecurityPolicyException {
            if (directive == CSPDirective.REPORT_TO) {
                throw new ContentSecurityPolicyException("provided directive must have a ReportingEndpoint instance");
            }
            if (host == null) {
                throw new ContentSecurityPolicyException("host cannot be null");
            }
            this.hosts.add(host);
            return this;
        }

        public ContentSecurityPolicyBuilder host(ReportingEndpoint host)
                throws ContentSecurityPolicyException {
            if (directive != CSPDirective.REPORT_TO) {
                throw new ContentSecurityPolicyException("provided directive must have a String host/endpoint");
            }
            if (host == null) {
                throw new ContentSecurityPolicyException("host cannot be null");
            }
            this.hosts.add(host);
            return this;
        }

        public ContentSecurityPolicy build()
                throws ContentSecurityPolicyException {
            if (hosts.isEmpty()) {
                throw new ContentSecurityPolicyException("no host/endpoint provided");
            }
            return new ContentSecurityPolicy(this);
        }
    }
}
