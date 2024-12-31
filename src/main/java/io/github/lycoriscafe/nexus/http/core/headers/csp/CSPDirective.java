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
    BASE_URI("base-uri"),
    CHILD_SRC("child-src"),
    CONNECT_SRC("connect-src"),
    DEFAULT_SRC("default-src"),
    FENCED_FRAME_SRC("fenced-frame-src"),
    FONT_SRC("font-src"),
    FRAME_SRC("frame-src"),
    IMG_SRC("img-src"),
    MANIFEST_SRC("manifest-src"),
    MEDIA_SRC("media-src"),
    OBJECT_SRC("object-src"),
    PREFETCH_SRC("prefetch-src"),
    SCRIPT_SRC("script-src"),
    SCRIPT_SRC_ELEM("script-src-elem"),
    SCRIPT_SRC_ATTR("script-src-attr"),
    STYLE_SRC("style-src"),
    STYLE_SRC_ELEM("style-src-elem"),
    STYLE_SRC_ATTR("style-src-attr"),
    WORKER_SRC("worker-src"),
    SANDBOX("sandbox"),
    FORM_ACTION("form-action"),
    FRAME_ANCESTORS("frame-ancestors"),
    UPGRADE_INSECURE_REQUESTS("upgrade-insecure-requests"),
    REPORT_URI("report-uri"),
    REPORT_TO("report-to");

    private final String name;

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
