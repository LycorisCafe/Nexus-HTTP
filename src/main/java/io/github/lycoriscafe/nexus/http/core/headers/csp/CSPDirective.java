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

package io.github.lycoriscafe.nexus.http.core.headers.csp;

/**
 * Content Security Policy (CSP) directives.
 *
 * @see ContentSecurityPolicy
 * @see ContentSecurityPolicyReportOnly
 * @see ReportingEndpoint
 * @see <a href="https://www.w3.org/TR/CSP">Content Security Policy (w3c)</a>
 * @since v1.0.0
 */
public enum CSPDirective {
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    BASE_URI("base-uri"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    CHILD_SRC("child-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    CONNECT_SRC("connect-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    DEFAULT_SRC("default-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    FENCED_FRAME_SRC("fenced-frame-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    FONT_SRC("font-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    FRAME_SRC("frame-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    IMG_SRC("img-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    MANIFEST_SRC("manifest-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    MEDIA_SRC("media-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    OBJECT_SRC("object-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    PREFETCH_SRC("prefetch-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    SCRIPT_SRC("script-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    SCRIPT_SRC_ELEM("script-src-elem"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    SCRIPT_SRC_ATTR("script-src-attr"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    STYLE_SRC("style-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    STYLE_SRC_ELEM("style-src-elem"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    STYLE_SRC_ATTR("style-src-attr"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    WORKER_SRC("worker-src"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    SANDBOX("sandbox"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    FORM_ACTION("form-action"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    FRAME_ANCESTORS("frame-ancestors"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    UPGRADE_INSECURE_REQUESTS("upgrade-insecure-requests"),
    /**
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    REPORT_URI("report-uri"),
    /**
     * When using this directive, API users should specify an instance of {@code ReportingEndpoint} to {@code HttpResponse}.
     *
     * @see ReportingEndpoint
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse HttpResponse
     * @see <a href="https://www.w3.org/TR/CSP/#csp-directives">Content Security Policy Directives (w3c)</a>
     * @see CSPDirective
     * @since v1.0.0
     */
    REPORT_TO("report-to");

    private final String name;

    /**
     * CSP Directive.
     *
     * @param name String to set in header
     * @see CSPDirective
     * @since v1.0.0
     */
    CSPDirective(final String name) {
        this.name = name;
    }

    /**
     * Get header value string of provided CSP directive.
     *
     * @return Header value string
     * @see CSPDirective
     * @since v1.0.0
     */
    public String getName() {
        return name;
    }
}
