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

/**
 * Cookie {@code SameSite} values.
 *
 * @see Cookie
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies#controlling_third-party_cookies_with_samesite">Controlling third-party
 * cookies with {@code SameSite} (MDN Docs)</a>
 * @since v1.0.0
 */
public enum CookieSameSite {
    /**
     * {@code Strict} causes the browser to only send the cookie in response to requests originating from the cookie's origin site. This should be
     * used when you have cookies relating to functionality that will always be behind an initial navigation, such as authentication or storing
     * shopping cart information.
     *
     * @see CookieSameSite
     * @since v1.0.0
     */
    STRICT("Strict"),
    /**
     * {@code Lax} is similar, except the browser also sends the cookie when the user navigates to the cookie's origin site (even if the user is
     * coming from a different site). This is useful for cookies affecting the display of a site — for example, you might have partner product
     * information along with an affiliate link on your website. When that link is followed to the partner website, they might want to set a cookie
     * stating that the affiliate link was followed, which displays a reward banner and provides a discount if the product is purchased.
     *
     * @see CookieSameSite
     * @since v1.0.0
     */
    LAX("Lax"),
    /**
     * {@code None} specifies that cookies are sent on both originating and cross-site requests. This is useful if you want to send cookies along with
     * requests made from third-party content embedded in other sites, for example, ad-tech or analytics providers. Note that if {@code SameSite=None}
     * is set then the {@code Secure} attribute must also be set — {@code SameSite=None} requires a secure context.
     *
     * @see CookieSameSite
     * @since v1.0.0
     */
    NONE("None");

    private final String value;

    CookieSameSite(String value) {
        this.value = value;
    }

    /**
     * Get {@code SameSite} value for include in HTTP header.
     *
     * @return Formatted {@code SameSite} value
     * @see CookieSameSite
     * @since v1.0.0
     */
    public String getValue() {
        return value;
    }
}
