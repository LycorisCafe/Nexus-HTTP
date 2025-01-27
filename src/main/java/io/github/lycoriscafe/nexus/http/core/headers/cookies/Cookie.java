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

package io.github.lycoriscafe.nexus.http.core.headers.cookies;

import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.util.List;
import java.util.Objects;

/**
 * Cookie for HTTP request/response.
 * <pre>
 *     {@code
 *     // Example codes
 *     var cookie1 = new Cookie("MySweetCookie", "Yum Yum Yum");
 *     var cookie2 = new Cookie("MyAnotherCookie", "cookie!!!")
 *          .setMaxAge(3600L);
 *     }
 * </pre>
 *
 * @see io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq.HttpRequest HttpRequest
 * @see io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes.HttpResponse HttpResponse
 * @see <a href="https://datatracker.ietf.org/doc/rfc6265">HTTP State Management Mechanism (rfc6265)</a>
 * @since v1.0.0
 */
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

    /**
     * Create an instance of {@code Cookie}
     *
     * @param cookieName  Cookie name
     * @param cookieValue Cookie value
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie(final String cookieName,
                  final String cookieValue) {
        name = cookieName;
        value = cookieValue;
    }

    /**
     * Get the name of the cookie.
     *
     * @return Name of the cookie
     * @see #Cookie(String, String)
     * @see Cookie
     * @since v1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Get value of the cookie.
     *
     * @return Value of the cookie
     * @see #Cookie(String, String)
     * @see Cookie
     * @since v1.0.0
     */
    public String getValue() {
        return value;
    }

    /**
     * Set {@code Expires} directive value. The argument passed to this method should be HTTP date formatted string.
     *
     * @param expires HTTP date formatted string
     * @return Same {@code Cookie} instance
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9110#name-date-time-formats">HTTP Semantics (rfc9110) - 5.6.7. Date/Time Formats</a>
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie setExpires(final String expires) {
        this.expires = expires;
        return this;
    }

    /**
     * Get {@code Expires} directive value.
     *
     * @return Directive value
     * @see #setExpires(String)
     * @see Cookie
     * @since v1.0.0
     */
    public String getExpires() {
        return expires;
    }

    /**
     * Set {@code Max-Age} directive value.
     *
     * @param maxAge Value in seconds
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    /**
     * Get {@code Max-Age} directive value.
     *
     * @return Directive value
     * @see #setMaxAge(long)
     * @see Cookie
     * @since v1.0.0
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Set {@code Secure} directive.
     *
     * @param secure Directive status
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie setSecure(final boolean secure) {
        this.secure = secure;
        return this;
    }

    /**
     * Get {@code Secure} directive status.
     *
     * @return Directive status
     * @see #setSecure(boolean)
     * @see Cookie
     * @since v1.0.0
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Set {@code HttpOnly} directive.
     *
     * @param httpOnly Directive status
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    /**
     * Get {@code HttpOnly} directive status.
     *
     * @return Directive status
     * @see #setHttpOnly(boolean)
     * @see Cookie
     * @since v1.0.0
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Set {@code Domain} directive value.
     *
     * @param domain Directive value
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie setDomain(final String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Get {@code Domain} directive value.
     *
     * @return Directive value
     * @see #setDomain(String)
     * @see Cookie
     * @since v1.0.0
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set {@code Path} directive value.
     *
     * @param path Directive value
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @since v1.0.0
     */
    public Cookie setPath(final String path) {
        this.path = path;
        return this;
    }

    /**
     * Get {@code Path} directive value.
     *
     * @return Directive value
     * @see #setPath(String)
     * @see Cookie
     * @since v1.0.0
     */
    public String getPath() {
        return path;
    }

    /**
     * Set {@code SameSite} directive value.
     *
     * @param sameSite Directive value
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @see CookieSameSite
     * @since v1.0.0
     */
    public Cookie setSameSite(final CookieSameSite sameSite) {
        this.sameSite = Objects.requireNonNull(sameSite);
        if (sameSite == CookieSameSite.NONE) setSecure(true);
        return this;
    }

    /**
     * Get {@code SameSite} directive value.
     *
     * @return Directive value
     * @see #setSameSite(CookieSameSite)
     * @see Cookie
     * @see CookieSameSite
     * @since v1.0.0
     */
    public CookieSameSite getSameSite() {
        return sameSite;
    }

    /**
     * Set the cookie name prefix.
     *
     * @param prefix Name prefix
     * @return Same {@code Cookie} instance
     * @see Cookie
     * @see CookiePrefix
     * @since v1.0.0
     */
    public Cookie setPrefix(final CookiePrefix prefix) {
        this.prefix = Objects.requireNonNull(prefix);
        return this;
    }

    /**
     * Get cookie name prefix
     *
     * @return Name prefix
     * @see #setPrefix(CookiePrefix)
     * @see Cookie
     * @see CookiePrefix
     * @since v1.0.0
     */
    public CookiePrefix getPrefix() {
        return prefix;
    }

    /**
     * Process incoming cookies.
     *
     * @param headerValue {@code Cookie} header values came along with {@code HttpRequest}
     * @return {@code List} of {@code Cookie} that come along with the request
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq.HttpRequest HttpRequest
     * @see Cookie
     * @since v1.0.0
     */
    public static List<Cookie> parseIncomingCookies(final String headerValue) {
        String[] keyVal = headerValue.split(";", 0);
        List<Cookie> cookies = new NonDuplicateList<>();
        for (String string : keyVal) {
            String[] parts = string.split("=", 2);
            cookies.add(new Cookie(parts[0].trim(), parts[1].trim()));
        }
        return cookies;
    }

    /**
     * Process outgoing cookies.
     *
     * @param cookies {@code List} of {@code Cookie} that send along with {@code HttpResponse}
     * @return {@code Set-Cookie} header string
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes.HttpResponse HttpResponse
     * @see Cookie
     * @since v1.0.0
     */
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
