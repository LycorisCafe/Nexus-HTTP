/*
 * Copyright 2025 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.core.headers.cache;

/**
 * Cache Control for HTTP response.
 * <pre>
 *     {@code
 *     // Example code 1
 *     var cacheControl = new CacheControl()
 *          .setMaxAge(3600L).setMustRevalidate(true);
 *     }
 *     {@code
 *     // Example code 2
 *     var cacheControl = new CacheControl()
 *          .setNoCache(true);
 *     }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://datatracker.ietf.org/doc/rfc9111">HTTP Caching (rfc9111)</a>
 * @since v1.0.0
 */
public final class CacheControl {
    private Long maxAge;
    private Long sMaxAge;
    private boolean noCache;
    private boolean mustRevalidate;
    private boolean proxyRevalidate;
    private boolean noStore;
    private boolean private_;
    private boolean public_;
    private boolean mustUnderstand;
    private boolean noTransform;
    private boolean immutable;
    private Long staleWhileRevalidate;
    private Long staleIfError;

    /**
     * Create instance of {@code CacheControl}.
     *
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl() {}

    /**
     * Get {@code max-age} directive value.
     *
     * @return {@code max-age} directive value
     * @see #setMaxAge(long)
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getMaxAge() {
        return maxAge;
    }

    /**
     * Set {@code max-age} directive value in <b>seconds</b>.
     *
     * @param maxAge {@code max-age} directive value in <b>seconds</b>
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    /**
     * Get {@code s-max-age} directive value.
     *
     * @return {@code s-max-age} directive value
     * @see #setSMaxAge(long)
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getSMaxAge() {
        return sMaxAge;
    }

    /**
     * Set {@code s-max-age} directive value in <b>seconds</b>.
     *
     * @param sMaxAge {@code s-max-age} directive value in <b>seconds</b>
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setSMaxAge(final long sMaxAge) {
        this.sMaxAge = sMaxAge;
        return this;
    }

    /**
     * Get {@code no-cache} directive status.
     *
     * @return {@code no-cache} directive status
     * @see #setNoCache(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isNoCache() {
        return noCache;
    }

    /**
     * Set {@code no-cache} directive status.
     *
     * @param noCache {@code no-cache} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setNoCache(final boolean noCache) {
        this.noCache = noCache;
        return this;
    }

    /**
     * Get {@code must-revalidate} directive status.
     *
     * @return {@code must-revalidate} directive status
     * @see #setMustRevalidate(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isMustRevalidate() {
        return mustRevalidate;
    }

    /**
     * Set {@code must-revalidate} directive status.
     *
     * @param mustRevalidate {@code must-revalidate} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
        return this;
    }

    /**
     * Get {@code proxy-revalidate} directive status.
     *
     * @return {@code proxy-revalidate} directive status
     * @see #setProxyRevalidate(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isProxyRevalidate() {
        return proxyRevalidate;
    }

    /**
     * Set {@code proxy-revalidate} directive status.
     *
     * @param proxyRevalidate {@code proxy-revalidate} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
        return this;
    }

    /**
     * Get {@code no-store} directive status.
     *
     * @return {@code no-store} directive status
     * @see #setNoStore(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isNoStore() {
        return noStore;
    }

    /**
     * Set {@code no-store} directive status.
     *
     * @param noStore {@code no-store} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setNoStore(final boolean noStore) {
        this.noStore = noStore;
        return this;
    }

    /**
     * Get {@code private} directive status.
     *
     * @return {@code private} directive status
     * @see #setPrivate(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isPrivate() {
        return private_;
    }

    /**
     * Set {@code private} directive status.
     *
     * @param private_ {@code private} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setPrivate(final boolean private_) {
        this.private_ = private_;
        return this;
    }

    /**
     * Get {@code public} directive status.
     *
     * @return {@code public} directive status
     * @see #setPublic(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isPublic() {
        return public_;
    }

    /**
     * Set {@code public} directive status.
     *
     * @param public_ {@code public} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setPublic(final boolean public_) {
        this.public_ = public_;
        return this;
    }

    /**
     * Get {@code must-understand} directive status.
     *
     * @return {@code must-understand} directive status
     * @see #setMustUnderstand(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    /**
     * Set {@code must-understand} directive status.
     *
     * @param mustUnderstand {@code must-understand} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setMustUnderstand(final boolean mustUnderstand) {
        noStore = true;
        this.mustUnderstand = mustUnderstand;
        return this;
    }

    /**
     * Get {@code no-transform} directive status.
     *
     * @return {@code no-transform} directive status
     * @see #setNoTransform(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isNoTransform() {
        return noTransform;
    }

    /**
     * Set {@code no-transform} directive status.
     *
     * @param noTransform {@code no-transform} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
        return this;
    }

    /**
     * Get {@code immutable} directive status.
     *
     * @return {@code immutable} directive status
     * @see #setImmutable(boolean)
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isImmutable() {
        return immutable;
    }

    /**
     * Set {@code immutable} directive status.
     *
     * @param immutable {@code immutable} directive status
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setImmutable(final boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /**
     * Get provided {@code stale-while-revalidate} directive value.
     *
     * @return {@code stale-while-revalidate} directive value
     * @see #setStaleWhileRevalidate(long)
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getStaleWhileRevalidate() {
        return staleWhileRevalidate;
    }

    /**
     * Set {@code stale-while-revalidate} directive value in <b>seconds</b>.
     *
     * @param staleWhileRevalidate {@code stale-while-revalidate} directive value in <b>seconds</b>
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setStaleWhileRevalidate(final long staleWhileRevalidate) {
        this.staleWhileRevalidate = staleWhileRevalidate;
        return this;
    }

    /**
     * Get {@code stale-if-error} directive value.
     *
     * @return {@code stale-if-error} directive value
     * @see #setStaleIfError(long)
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getStaleIfError() {
        return staleIfError;
    }

    /**
     * Set {@code stale-if-error} directive value in <b>seconds</b>.
     *
     * @param staleIfError {@code stale-if-error} directive value in <b>seconds</b>
     * @return Same {@code CacheControl} instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setStaleIfError(final long staleIfError) {
        this.staleIfError = staleIfError;
        return this;
    }

    /**
     * Process provided {@code CacheControl} instance as a {@code Cache-Control} HTTP header.
     *
     * @param cacheControl {@code CacheControl} instance
     * @return {@code Cache-Control} header string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see CacheControl
     * @since v1.0.0
     */
    public static String processOutgoingCacheControl(final CacheControl cacheControl) {
        if (cacheControl == null) return "";

        StringBuilder output = new StringBuilder().append("Cache-Control:");
        boolean anyDirectives = false;
        if (cacheControl.getMaxAge() != null) {
            output.append(" ").append("max-age=").append(cacheControl.getMaxAge());
            anyDirectives = true;
        }
        if (cacheControl.getSMaxAge() != null) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("s-max-age=").append(cacheControl.getSMaxAge());
            anyDirectives = true;
        }
        if (cacheControl.isNoCache()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("no-cache");
            anyDirectives = true;
        }
        if (cacheControl.isMustRevalidate()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("must-revalidate");
            anyDirectives = true;
        }
        if (cacheControl.isProxyRevalidate()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("proxy-revalidate");
            anyDirectives = true;
        }
        if (cacheControl.isNoStore()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("no-store");
            anyDirectives = true;
        }
        if (cacheControl.isPrivate()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("private");
            anyDirectives = true;
        }
        if (cacheControl.isPublic()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("public");
            anyDirectives = true;
        }
        if (cacheControl.isMustUnderstand()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("must-understand");
            anyDirectives = true;
        }
        if (cacheControl.isNoTransform()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("no-transform");
            anyDirectives = true;
        }
        if (cacheControl.isImmutable()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("immutable");
            anyDirectives = true;
        }
        if (cacheControl.getStaleWhileRevalidate() != null) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("stale-while-revalidate").append("=")
                    .append(cacheControl.getStaleWhileRevalidate());
            anyDirectives = true;
        }
        if (cacheControl.getStaleIfError() != null) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("stale-if-error").append("=").append(cacheControl.getStaleIfError());
            anyDirectives = true;
        }

        if (anyDirectives) {
            return output.append("\r\n").toString();
        }
        return "";
    }
}
