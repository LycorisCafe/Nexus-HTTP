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

package io.github.lycoriscafe.nexus.http.core.headers.cookies;

public final class Cookie {
    private final String name;
    private final String value;
    private final String expires;
    private final long maxAge;
    private final boolean secure;
    private final boolean httpOnly;
    private final String domain;
    private final String path;
    private final CookieSameSite sameSite;
    private final CookiePrefix prefix;

    private Cookie(CookieBuilder builder) {
        name = builder.name;
        value = builder.value;
        expires = builder.expires;
        maxAge = builder.maxAge;
        secure = builder.secure;
        httpOnly = builder.httpOnly;
        domain = builder.domain;
        path = builder.path;
        sameSite = builder.sameSite;
        prefix = builder.prefix;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getExpires() {
        return expires;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public CookieSameSite getSameSite() {
        return sameSite;
    }

    public CookiePrefix getPrefix() {
        return prefix;
    }

    public static CookieBuilder builder(String cookieName,
                                        String cookieValue) {
        return new CookieBuilder(cookieName, cookieValue);
    }


    public static final class CookieBuilder {
        private final String name;
        private final String value;
        private String expires;
        private long maxAge;
        private boolean secure;
        private boolean httpOnly;
        private String domain;
        private String path;
        private CookieSameSite sameSite;
        private CookiePrefix prefix;

        public CookieBuilder(String cookieName,
                             String cookieValue) {
            name = cookieName;
            value = cookieValue;
        }

        public CookieBuilder expires(String expires) {
            this.expires = expires;
            return this;
        }

        public CookieBuilder maxAge(long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public CookieBuilder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public CookieBuilder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public CookieBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public CookieBuilder path(String path) {
            this.path = path;
            return this;
        }

        public CookieBuilder sameSite(CookieSameSite sameSite) throws CookieException {
            if (this.sameSite == null) {
                throw new CookieException("Same Site cannot be null. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.sameSite = sameSite;
            return this;
        }

        public CookieBuilder prefix(CookiePrefix prefix) throws CookieException {
            if (prefix == null) {
                throw new CookieException("Cookie prefix cannot be null. " +
                        "If you need this to be null, just ignore this method.");
            }
            this.prefix = prefix;
            return this;
        }

        public Cookie build() {
            return new Cookie(this);
        }
    }
}
