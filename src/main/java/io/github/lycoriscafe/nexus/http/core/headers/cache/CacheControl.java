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
     * Get <code>max-age</code> directive value.
     *
     * @return <code>max-age</code> directive value
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getMaxAge() {
        return maxAge;
    }

    /**
     * Set <code>max-age</code> directive value in <b>seconds</b>.
     *
     * @param maxAge <code>max-age</code> directive value in <b>seconds</b>
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    /**
     * Get <code>s-max-age</code> directive value.
     *
     * @return <code>s-max-age</code> directive value
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getSMaxAge() {
        return sMaxAge;
    }

    /**
     * Set <code>s-max-age</code> directive value in <b>seconds</b>.
     *
     * @param sMaxAge <code>s-max-age</code> directive value in <b>seconds</b>
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setSMaxAge(final long sMaxAge) {
        this.sMaxAge = sMaxAge;
        return this;
    }

    /**
     * Get <code>no-cache</code> directive status.
     *
     * @return <code>no-cache</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isNoCache() {
        return noCache;
    }

    /**
     * Set <code>no-cache</code> directive status.
     *
     * @param noCache <code>no-cache</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setNoCache(final boolean noCache) {
        this.noCache = noCache;
        return this;
    }

    /**
     * Get <code>must-revalidate</code> directive status.
     *
     * @return <code>must-revalidate</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isMustRevalidate() {
        return mustRevalidate;
    }

    /**
     * Set <code>must-revalidate</code> directive status.
     *
     * @param mustRevalidate <code>must-revalidate</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
        return this;
    }

    /**
     * Get <code>proxy-revalidate</code> directive status.
     *
     * @return <code>proxy-revalidate</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isProxyRevalidate() {
        return proxyRevalidate;
    }

    /**
     * Set <code>proxy-revalidate</code> directive status.
     *
     * @param proxyRevalidate <code>proxy-revalidate</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
        return this;
    }

    /**
     * Get <code>no-store</code> directive status.
     *
     * @return <code>no-store</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isNoStore() {
        return noStore;
    }

    /**
     * Set <code>no-store</code> directive status.
     *
     * @param noStore <code>no-store</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setNoStore(final boolean noStore) {
        this.noStore = noStore;
        return this;
    }

    /**
     * Get <code>private</code> directive status.
     *
     * @return <code>private</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isPrivate_() {
        return private_;
    }

    /**
     * Set <code>private</code> directive status.
     *
     * @param private_ <code>private</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setPrivate_(final boolean private_) {
        this.private_ = private_;
        return this;
    }

    /**
     * Get <code>public</code> directive status.
     *
     * @return <code>public</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isPublic_() {
        return public_;
    }

    /**
     * Set <code>public</code> directive status.
     *
     * @param public_ <code>public</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setPublic_(final boolean public_) {
        this.public_ = public_;
        return this;
    }

    /**
     * Get <code>must-understand</code> directive status.
     *
     * @return <code>must-understand</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    /**
     * Set <code>must-understand</code> directive status.
     *
     * @param mustUnderstand <code>must-understand</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setMustUnderstand(final boolean mustUnderstand) {
        noStore = true;
        this.mustUnderstand = mustUnderstand;
        return this;
    }

    /**
     * Get <code>no-transform</code> directive status.
     *
     * @return <code>no-transform</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isNoTransform() {
        return noTransform;
    }

    /**
     * Set <code>no-transform</code> directive status.
     *
     * @param noTransform <code>no-transform</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
        return this;
    }

    /**
     * Get <code>immutable</code> directive status.
     *
     * @return <code>immutable</code> directive status
     * @see CacheControl
     * @since v1.0.0
     */
    public boolean isImmutable() {
        return immutable;
    }

    /**
     * Set <code>immutable</code> directive status.
     *
     * @param immutable <code>immutable</code> directive status
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setImmutable(final boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /**
     * Get provided <code>stale-while-revalidate</code> directive value.
     *
     * @return <code>stale-while-revalidate</code> directive value
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getStaleWhileRevalidate() {
        return staleWhileRevalidate;
    }

    /**
     * Set <code>stale-while-revalidate</code> directive value in <b>seconds</b>.
     *
     * @param staleWhileRevalidate <code>stale-while-revalidate</code> directive value in <b>seconds</b>
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setStaleWhileRevalidate(final long staleWhileRevalidate) {
        this.staleWhileRevalidate = staleWhileRevalidate;
        return this;
    }

    /**
     * Get <code>stale-if-error</code> directive value.
     *
     * @return <code>stale-if-error</code> directive value
     * @see CacheControl
     * @since v1.0.0
     */
    public Long getStaleIfError() {
        return staleIfError;
    }

    /**
     * Set <code>stale-if-error</code> directive value in <b>seconds</b>.
     *
     * @param staleIfError <code>stale-if-error</code> directive value in <b>seconds</b>
     * @return Same <code>CacheControl</code> instance
     * @see CacheControl
     * @since v1.0.0
     */
    public CacheControl setStaleIfError(final long staleIfError) {
        this.staleIfError = staleIfError;
        return this;
    }

    /**
     * Process provided <code>CacheControl</code> instance as a <code>Cache-Control</code> HTTP header.
     *
     * @param cacheControl <code>CacheControl</code> instance
     * @return <code>Cache-Control</code> header string
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
        if (cacheControl.isPrivate_()) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("private");
            anyDirectives = true;
        }
        if (cacheControl.isPublic_()) {
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
