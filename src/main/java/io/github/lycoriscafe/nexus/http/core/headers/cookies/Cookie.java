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

public class Cookie {
    private final CookieOperation operation;
    private final String name;
    private final String value;
    private final String expires;
    private final long maxAge;
    private final boolean secure;
    private final boolean httpOnly;
    private final String domain;
    private final String path;
    private final SameSite sameSite;
    private final CookiePrefix prefix;

    private Cookie(CookieBuilder builder) {
        this.operation = builder.operation;
        this.name = builder.name;
        this.value = builder.value;
        this.expires = builder.expires;
        this.maxAge = builder.maxAge;
        this.secure = builder.secure;
        this.httpOnly = builder.httpOnly;
        this.domain = builder.domain;
        this.path = builder.path;
        this.sameSite = builder.sameSite;
        this.prefix = builder.prefix;
    }

    public CookieOperation getOperation() {
        return operation;
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

    public SameSite getSameSite() {
        return sameSite;
    }

    public CookiePrefix getPrefix() {
        return prefix;
    }

    public static CookieBuilder builder(CookieOperation operation,
                                        String cookieName,
                                        String cookieValue) {
        return new CookieBuilder(operation, cookieName, cookieValue);
    }


    public static class CookieBuilder {
        private final CookieOperation operation;
        private final String name;
        private final String value;
        private String expires;
        private long maxAge;
        private boolean secure;
        private boolean httpOnly;
        private String domain;
        private String path;
        private SameSite sameSite;
        private CookiePrefix prefix;

        public CookieBuilder(CookieOperation operation,
                             String cookieName,
                             String cookieValue) {
            this.operation = operation;
            this.name = cookieName;
            this.value = cookieValue;
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

        public CookieBuilder sameSite(SameSite sameSite) throws CookieException {
            if (this.sameSite == null) {
                throw new CookieException("Same Site cannot be null");
            }
            this.sameSite = sameSite;
            return this;
        }

        public CookieBuilder prefix(CookiePrefix prefix) throws CookieException {
            if (prefix == null) {
                throw new CookieException("Cookie prefix cannot be null");
            }
            this.prefix = prefix;
            return this;
        }

        public Cookie build() {
            return new Cookie(this);
        }
    }
}
