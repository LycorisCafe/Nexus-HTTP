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
import java.util.List;

public sealed class ContentSecurityPolicy permits ContentSecurityPolicyReportOnly {
    private final CSPDirective directive;
    private final HashSet<String> values;

    public ContentSecurityPolicy(final CSPDirective directive,
                                 final HashSet<String> values) {
        if (directive == null || values == null || values.isEmpty()) {
            throw new NullPointerException("directive/values cannot be null");
        }
        this.directive = directive;
        this.values = values;
    }

    public CSPDirective getDirective() {
        return directive;
    }

    public List<String> getValues() {
        return values.stream().toList();
    }

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
