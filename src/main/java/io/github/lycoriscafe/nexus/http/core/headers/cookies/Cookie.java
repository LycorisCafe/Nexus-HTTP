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
    private String expires;
    private long maxAge;
    private boolean secure;
    private boolean httpOnly;
    private String domain;
    private String path;
    private CookieSameSite sameSite;
    private CookiePrefix prefix;

    private Cookie(final String cookieName,
                   final String cookieValue) {
        name = cookieName;
        value = cookieValue;
    }

    public Cookie expires(final String expires) {
        this.expires = expires;
        return this;
    }

    public Cookie maxAge(final long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public Cookie secure(final boolean secure) {
        this.secure = secure;
        return this;
    }

    public Cookie httpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public Cookie domain(final String domain) {
        this.domain = domain;
        return this;
    }

    public Cookie path(final String path) {
        this.path = path;
        return this;
    }

    public Cookie sameSite(final CookieSameSite sameSite) throws CookieException {
        if (this.sameSite == null) {
            throw new CookieException("Same Site cannot be null. " +
                    "If you need this to be null, just ignore this method.");
        }
        this.sameSite = sameSite;
        return this;
    }

    public Cookie prefix(final CookiePrefix prefix) throws CookieException {
        if (prefix == null) {
            throw new CookieException("Cookie prefix cannot be null. " +
                    "If you need this to be null, just ignore this method.");
        }
        this.prefix = prefix;
        return this;
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

    public static Cookie[] processIncomingCookies(String headerValue) {
        String[] keyVal = headerValue.split(";", 0);
        Cookie[] cookies = new Cookie[keyVal.length];
        for (int i = 0; i < keyVal.length; i++) {
            String[] parts = keyVal[i].split("=", 2);
            cookies[i] = new Cookie(parts[0].trim(), parts[1].trim());
        }
        return cookies;
    }
}
