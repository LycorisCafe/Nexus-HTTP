/*
 * Copyright 2024 Lycoris Café
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

/**
 * Cookie name prefix.
 *
 * @see Cookie
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies#cookie_prefixes">Cookie Prefixes (MDN Docs)</a>
 * @since v1.0.0
 */
public enum CookiePrefix {
    /**
     * If a cookie name has this prefix, it's accepted in a {@code Set-Cookie} header only if it's also marked with the {@code Secure} attribute, was
     * sent from a secure origin, does not include a {@code Domain} attribute, and has the {@code Path} attribute set to {@code /}. In other words,
     * the cookie is domain-locked.
     *
     * @see Cookie
     * @since v1.0.0
     */
    HOST("__Host-"),
    /**
     * If a cookie name has this prefix, it's accepted in a {@code Set-Cookie} header only if it's marked with the {@code Secure} attribute and was
     * sent from a secure origin. This is weaker than the {@code __Host-} prefix.
     *
     * @see Cookie
     * @since v1.0.0
     */
    SECURE("__Secure-");

    private final String prefix;

    CookiePrefix(final String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get prefix value for include in the HTTP header.
     *
     * @return Formatted prefix value
     * @see Cookie
     * @since v1.0.0
     */
    public String getPrefix() {
        return prefix;
    }
}
