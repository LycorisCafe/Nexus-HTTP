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

import java.util.HashSet;

public sealed class ContentSecurityPolicy permits ContentSecurityPolicyReportOnly {
    private final CSPDirective directive;
    private HashSet<CSPValue> values;
    final HashSet<Object> hosts;

    ContentSecurityPolicy(final CSPDirective directive) throws ContentSecurityPolicyException {
        if (directive == null) {
            throw new ContentSecurityPolicyException("directive cannot be null");
        }
        this.directive = directive;
        hosts = new HashSet<>();
    }

    public ContentSecurityPolicy value(final CSPValue value) throws ContentSecurityPolicyException {
        if (directive == CSPDirective.REPORT_TO || directive == CSPDirective.REPORT_URI) {
            throw new ContentSecurityPolicyException("report-to & report-uri cannot be contain any value");
        }
        if (values == null) {
            values = new HashSet<>();
        }
        this.values.add(value);
        return this;
    }

    public ContentSecurityPolicy host(final String host) throws ContentSecurityPolicyException {
        if (directive == CSPDirective.REPORT_TO) {
            throw new ContentSecurityPolicyException("provided directive must have a ReportingEndpoint instance");
        }
        if (host == null) {
            throw new ContentSecurityPolicyException("host cannot be null");
        }
        this.hosts.add(host);
        return this;
    }

    public ContentSecurityPolicy host(final ReportingEndpoint host) throws ContentSecurityPolicyException {
        if (directive != CSPDirective.REPORT_TO) {
            throw new ContentSecurityPolicyException("provided directive must have a String host/endpoint");
        }
        if (host == null) {
            throw new ContentSecurityPolicyException("host cannot be null");
        }
        this.hosts.add(host);
        return this;
    }

    public CSPDirective getDirective() {
        return directive;
    }

    public HashSet<CSPValue> getValues() {
        return values;
    }

    public HashSet<Object> getHosts() {
        return hosts;
    }

//    public static ContentSecurityPolicy builder(CSPDirective directive)
//            throws ContentSecurityPolicyException {
//        return new ContentSecurityPolicy(directive);
//    }
//
//    public static sealed class ContentSecurityPolicy
//            permits ContentSecurityPolicyReportOnly.ContentSecurityPolicyReportOnlyBuilder {
//
//
//        public ContentSecurityPolicy build()
//                throws ContentSecurityPolicyException {
//            if (hosts.isEmpty()) {
//                throw new ContentSecurityPolicyException("no host/endpoint provided");
//            }
//            return new ContentSecurityPolicy(this);
//        }
//    }
}
