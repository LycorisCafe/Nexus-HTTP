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

public final class CacheControl {
    private long maxAge = -1L;
    private long sMaxAge = -1L;
    private boolean noCache;
    private boolean mustRevalidate;
    private boolean proxyRevalidate;
    private boolean noStore;
    private boolean private_;
    private boolean public_;
    private boolean mustUnderstand;
    private boolean noTransform;
    private boolean immutable;
    private long staleWhileRevalidate = -1L;
    private long staleIfError = -1L;

    public long getMaxAge() {
        return maxAge;
    }

    public CacheControl setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public long getsMaxAge() {
        return sMaxAge;
    }

    public CacheControl setsMaxAge(final long sMaxAge) {
        this.sMaxAge = sMaxAge;
        return this;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public CacheControl setNoCache(final boolean noCache) {
        this.noCache = noCache;
        return this;
    }

    public boolean isMustRevalidate() {
        return mustRevalidate;
    }

    public CacheControl setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
        return this;
    }

    public boolean isProxyRevalidate() {
        return proxyRevalidate;
    }

    public CacheControl setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
        return this;
    }

    public boolean isNoStore() {
        return noStore;
    }

    public CacheControl setNoStore(final boolean noStore) {
        this.noStore = noStore;
        return this;
    }

    public boolean isPrivate_() {
        return private_;
    }

    public CacheControl setPrivate_(final boolean private_) {
        this.private_ = private_;
        return this;
    }

    public boolean isPublic_() {
        return public_;
    }

    public CacheControl setPublic_(final boolean public_) {
        this.public_ = public_;
        return this;
    }

    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    public CacheControl setMustUnderstand(final boolean mustUnderstand) {
        noStore = true;
        this.mustUnderstand = mustUnderstand;
        return this;
    }

    public boolean isNoTransform() {
        return noTransform;
    }

    public CacheControl setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
        return this;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public CacheControl setImmutable(final boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    public long getStaleWhileRevalidate() {
        return staleWhileRevalidate;
    }

    public CacheControl setStaleWhileRevalidate(final long staleWhileRevalidate) {
        this.staleWhileRevalidate = staleWhileRevalidate;
        return this;
    }

    public long getStaleIfError() {
        return staleIfError;
    }

    public CacheControl setStaleIfError(final long staleIfError) {
        this.staleIfError = staleIfError;
        return this;
    }

    public static String processOutgoingCacheControl(final CacheControl cacheControl) {
        if (cacheControl == null) return "";

        StringBuilder output = new StringBuilder().append("Cache-Control:");
        boolean anyDirectives = false;
        if (cacheControl.getMaxAge() > -1L) {
            output.append(" ").append("max-age=").append(cacheControl.getMaxAge());
            anyDirectives = true;
        }
        if (cacheControl.getsMaxAge() > -1L) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("s-max-age=").append(cacheControl.getsMaxAge());
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
        if (cacheControl.getStaleWhileRevalidate() > -1L) {
            if (anyDirectives) output.append(",");
            output.append(" ").append("stale-while-revalidate").append("=")
                    .append(cacheControl.getStaleWhileRevalidate());
            anyDirectives = true;
        }
        if (cacheControl.getStaleIfError() > -1L) {
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
