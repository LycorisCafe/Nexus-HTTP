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

public final class ContentSecurityPolicyReportOnly<T>
        extends ContentSecurityPolicy<T> {
    public ContentSecurityPolicyReportOnly(ContentSecurityPolicyReportOnlyBuilder<T> builder) {
        super(builder);
    }

    public static <T> ContentSecurityPolicyReportOnlyBuilder<T> builder(CSPDirective directive) {
        return new ContentSecurityPolicyReportOnlyBuilder<>(directive);
    }

    public static final class ContentSecurityPolicyReportOnlyBuilder<T>
            extends ContentSecurityPolicyBuilder<T> {
        public ContentSecurityPolicyReportOnlyBuilder(CSPDirective directive) {
            super(directive);
        }

        @Override
        public ContentSecurityPolicyReportOnly<T> build() throws ContentSecurityPolicyException {
            if (super.hosts.isEmpty()) {
                throw new ContentSecurityPolicyException("no host provided");
            }
            return new ContentSecurityPolicyReportOnly<>(this);
        }
    }
}
