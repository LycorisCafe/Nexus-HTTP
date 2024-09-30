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

public sealed class ContentSecurityPolicy<T>
        permits ContentSecurityPolicyReportOnly {
    private final CSPDirective directive;
    private final List<CSPValue> values;
    private final List<T> hosts;

    ContentSecurityPolicy(ContentSecurityPolicyBuilder<T> builder) {
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

    public List<T> getHosts() {
        return hosts;
    }

    public static <T> ContentSecurityPolicyBuilder<T> builder(CSPDirective directive) {
        return new ContentSecurityPolicyBuilder<>(directive);
    }

    public static sealed class ContentSecurityPolicyBuilder<T>
            permits ContentSecurityPolicyReportOnly.ContentSecurityPolicyReportOnlyBuilder {
        final CSPDirective directive;
        private final List<CSPValue> values;
        final List<T> hosts;

        public ContentSecurityPolicyBuilder(CSPDirective directive) {
            this.directive = directive;
            values = new ArrayList<>();
            hosts = new ArrayList<>();
        }

        public ContentSecurityPolicyBuilder<T> value(CSPValue value) throws ContentSecurityPolicyException {
            if (directive == CSPDirective.REPORT_TO || directive == CSPDirective.REPORT_URI) {
                throw new ContentSecurityPolicyException("report-to & report-uri cannot be contain any value");
            }
            this.values.add(value);
            return this;
        }

        public ContentSecurityPolicyBuilder<T> host(T host) throws ContentSecurityPolicyException {
            if (!(host instanceof String || host instanceof ReportingEndpoint)) {
                throw new ContentSecurityPolicyException("invalid host/endpoint");
            }
            this.hosts.add(host);
            return this;
        }

        public ContentSecurityPolicy<T> build() throws ContentSecurityPolicyException {
            if (hosts.isEmpty()) {
                throw new ContentSecurityPolicyException("no host provided");
            }
            return new ContentSecurityPolicy<>(this);
        }
    }
}
