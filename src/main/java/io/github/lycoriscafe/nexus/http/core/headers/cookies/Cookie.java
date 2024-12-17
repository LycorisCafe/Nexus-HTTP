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

import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.util.List;

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

    public Cookie(final String cookieName,
                  final String cookieValue) {
        name = cookieName;
        value = cookieValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Cookie setExpires(final String expires) {
        this.expires = expires;
        return this;
    }

    public String getExpires() {
        return expires;
    }

    public Cookie setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public Cookie setSecure(final boolean secure) {
        this.secure = secure;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public Cookie setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public Cookie setDomain(final String domain) {
        this.domain = domain;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Cookie setPath(final String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Cookie setSameSite(final CookieSameSite sameSite) throws CookieException {
        if (this.sameSite == null) {
            throw new CookieException(
                    "Same Site cannot be null. If you need this to be null, just ignore this method.");
        }
        this.sameSite = sameSite;
        return this;
    }

    public CookieSameSite getSameSite() {
        return sameSite;
    }

    public Cookie setPrefix(final CookiePrefix prefix) throws CookieException {
        if (prefix == null) {
            throw new CookieException(
                    "Cookie prefix cannot be null. If you need this to be null, just ignore this method.");
        }
        this.prefix = prefix;
        return this;
    }

    public CookiePrefix getPrefix() {
        return prefix;
    }

    public static List<Cookie> parseIncomingCookies(final String headerValue) {
        String[] keyVal = headerValue.split(";", 0);
        List<Cookie> cookies = new NonDuplicateList<>();
        for (String string : keyVal) {
            String[] parts = string.split("=", 2);
            cookies.add(new Cookie(parts[0].trim(), parts[1].trim()));
        }
        return cookies;
    }

    public static String processOutgoingCookies(final List<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) return "";

        StringBuilder output = new StringBuilder();
        for (Cookie cookie : cookies) {
            output.append("Set-Cookie:").append(" ");

            if (cookie.getPrefix() != null) {
                output.append(cookie.getPrefix());
                cookie.setSecure(true);
                if (cookie.getPrefix() == CookiePrefix.HOST) {
                    cookie.setPath("/");
                }
            }

            output.append(cookie.getName()).append("=").append(cookie.getValue())
                    .append(cookie.getExpires() != null ? "; Expires=" + cookie.getExpires() : "")
                    .append(cookie.getMaxAge() > 0 ? "; Max-Age=" + cookie.getMaxAge() : "")
                    .append(cookie.isSecure() ? "; Secure" : "").append(cookie.isHttpOnly() ? "; HttpOnly" : "")
                    .append(cookie.getDomain() != null ? "; Domain=" + cookie.getDomain() : "")
                    .append(cookie.getPath() != null ? "; Path=" + cookie.getPath() : "")
                    .append(cookie.getSameSite() != null ? "; SameSite=" + cookie.getSameSite().getValue() : "")
                    .append("\r\n");
        }
        return output.toString();
    }
}
