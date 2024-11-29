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
        reportingEndpoint = new HashMap<>();
        reportingEndpoint.put(name, url);
        return this;
    }

    public Map<CSPDirective, HashSet<String>> getPolicy() {
        return policy;
    }

    public Map<String, String> getReportingEndpoint() {
        return reportingEndpoint;
    }
}
