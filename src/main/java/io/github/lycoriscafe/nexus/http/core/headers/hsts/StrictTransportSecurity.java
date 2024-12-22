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

/**
 * HTTP Strict Transport Security (HSTS) for HTTP responses.
 * <pre>
 *     {@code
 *     // Example code
 *     var hsts = new StrictTransportSecurity(3600L);
 *     }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6797">HTTP Strict Transport Security (HSTS) (rfc6797)</a>
 * @since v1.0.0
 */
public final class StrictTransportSecurity {
    private final long maxAge;
    private boolean includeSubdomains;
    private boolean preload;

    /**
     * Create an instance of <code>StrictTransportSecurity</code>.
     *
     * @param maxAge HSTS max age in seconds
     * @see StrictTransportSecurity
     * @since v1.0.0
     */
    private StrictTransportSecurity(final long maxAge) {
        if (maxAge < 1) throw new IllegalStateException("max-age cannot be less than 1");
        this.maxAge = maxAge;
    }

    /**
     * Get HSTS max age.
     *
     * @return HSTS max age
     * @see StrictTransportSecurity
     * @since v1.0.0
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Set include subdomains status.
     *
     * @param includeSubdomains include subdomains status
     * @return Same <code>StrictTransportSecurity</code> instance
     * @see StrictTransportSecurity
     * @since v1.0.0
     */
    public StrictTransportSecurity setIncludeSubdomains(final boolean includeSubdomains) {
        this.includeSubdomains = includeSubdomains;
        return this;
    }

    /**
     * Get include subdomains status.
     *
     * @return Include subdomains status
     * @see StrictTransportSecurity
     * @since v1.0.0
     */
    public boolean isIncludeSubdomains() {
        return includeSubdomains;
    }

    /**
     * Set preload status.
     *
     * @param preload Preload status
     * @return Same <code>StrictTransportSecurity</code> instance
     * @see StrictTransportSecurity
     * @since v1.0.0
     */
    public StrictTransportSecurity setPreload(final boolean preload) {
        this.preload = preload;
        return this;
    }

    /**
     * Get preload status
     *
     * @return Preload status
     * @see StrictTransportSecurity
     * @since v1.0.0
     */
    public boolean isPreload() {
        return preload;
    }

    /**
     * Process HSTS instance as a <code>Strict-Transport-Security</code> HTTP header.
     *
     * @param hsts <code>StrictTransportSecurity</code> instance
     * @return Processed <code>Strict-Transport-Security</code> HTTP header
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see StrictTransportSecurity
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
     * @since v1.0.0
     */
    public static String processOutgoingHSTS(final StrictTransportSecurity hsts) {
        if (hsts == null) return "";

        StringBuilder output = new StringBuilder().append("Strict-Transport-Security:")
                .append(" ").append(hsts.getMaxAge());
        if (hsts.isPreload()) {
            return output.append("; includeSubDomains").append("; preload").append("\r\n").toString();
        }
        if (hsts.isIncludeSubdomains()) {
            return output.append("; includeSubDomains").append("\r\n").toString();
        }
        return output.append("\r\n").toString();
    }
}
