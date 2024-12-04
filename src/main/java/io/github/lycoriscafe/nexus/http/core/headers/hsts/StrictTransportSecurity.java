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

package io.github.lycoriscafe.nexus.http.core.headers.hsts;

public final class StrictTransportSecurity {
    private final long maxAge;
    private boolean includeSubdomains;
    private boolean preload;

    private StrictTransportSecurity(final long maxAge) throws StrictTransportSecurityException {
        if (maxAge < 1) {
            throw new StrictTransportSecurityException("max-age cannot be less than 1");
        }
        this.maxAge = maxAge;
    }

    public StrictTransportSecurity includeSubdomains(final boolean includeSubdomains) {
        this.includeSubdomains = includeSubdomains;
        return this;
    }

    public StrictTransportSecurity preload(final boolean preload) {
        this.preload = preload;
        return this;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public boolean isIncludeSubdomains() {
        return includeSubdomains;
    }

    public boolean isPreload() {
        return preload;
    }

    public static String processOutgoingHSTS(final StrictTransportSecurity hsts) {
        if (hsts == null) return "";

        StringBuilder output = new StringBuilder().append("Strict-Transport-Security:").append(" ").append(hsts.maxAge);
        if (hsts.preload) {
            return output.append("; includeSubDomains").append("; preload").append("\r\n").toString();
        }
        if (hsts.includeSubdomains) {
            return output.append("; includeSubDomains").append("\r\n").toString();
        }
        return output.append("\r\n").toString();
    }
}
