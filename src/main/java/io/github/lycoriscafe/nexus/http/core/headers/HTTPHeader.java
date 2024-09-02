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

package io.github.lycoriscafe.nexus.http.core.headers;

public enum HTTPHeader {
    ACCEPT("Accept", "*/*"),
    ACCEPT_CH("Accept-CH", null),
    ACCEPT_CHARSET("Accept-Charset", "UTF-8"),
    ACCEPT_ENCODING("Accept-Encoding", null),
    ACCEPT_LANGUAGE("Accept-Language", "en-US"),
    ACCEPT_PATCH("Accept-Patch", "text/*"),
    ACCEPT_POST("Accept-Post", "*/*"),
    ACCEPT_RANGES("Accept-Ranges", "bytes"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("", ""),
    ACCESS_CONTROL_ALLOW_HEADERS("", ""),
    ACCESS_CONTROL_ALLOW_METHODS("", ""),
    ACCESS_CONTROL_ALLOW_ORIGIN("", ""),
    ACCESS_CONTROL_EXPOSE_HEADERS("", ""),
    ACCESS_CONTROL_MAX_AGE("", ""),
    ACCESS_CONTROL_REQUEST_HEADERS("", ""),
    ACCESS_CONTROL_REQUEST_METHOD("", ""),
    AGE("", ""),
    ALLOW("", ""),
    ALT_SVC("", ""),
    ALT_USED("", ""),
    ATTRIBUTION_REPORTING_ELIGIBLE("", ""),
    ATTRIBUTION_REPORTING_REGISTER_SOURCE("", ""),
    ATTRIBUTION_REPORTING_REGISTER_TRIGGER("", ""),
    AUTHORIZATION("", ""),
    CACHE_CONTROL("", ""),
    CLEAR_SITE_DATA("", ""),
    CONNECTION("", ""),
    CONTENT_DIGEST("", ""),
    CONTENT_DISPOSITION("", ""),
    CONTENT_DPR("", ""),
    CONTENT_ENCODING("", ""),
    CONTENT_LANGUAGE("", ""),
    CONTENT_LENGTH("", ""),
    CONTENT_LOCATION("", ""),
    CONTENT_RANGE("", ""),
    CONTENT_SECURITY_POLICY("", ""),
    CONTENT_SECURITY_POLICY_REPORT_ONLY("", ""),
    CONTENT_TYPE("", ""),
    COOKIE("", ""),
    CRITICAL_CH("", ""),
    CROSS_ORIGIN_EMBEDDER_POLICY("", ""),
    CROSS_ORIGIN_OPENER_POLICY("", ""),
    CROSS_ORIGIN_RESOURCE_POLICY("", ""),
    DATE("", ""),
    DEVICE_MEMORY("", ""),
    DIGEST("", ""),
    DNT("", ""),
    DOWNLINK("", ""),
    DPR("", ""),
    EARLY_DATA("", ""),
    ECT("", ""),
    ETAG("", ""),
    EXPECT("", ""),
    EXPECT_CT("", ""),
    EXPIRES("", ""),
    FORWARDED("", ""),
    FROM("", ""),
    HOST("", ""),
    IF_MATCH("", ""),
    IF_MODIFIED_SINCE("", ""),
    IF_NONE_MATCH("", ""),
    IF_RANGE("", ""),
    IF_UNMODIFIED_SINCE("", ""),
    KEEP_ALIVE("", ""),
    LAST_MODIFIED("", ""),
    LINK("", ""),
    LOCATION("", ""),
    MAX_FORWARDS("", ""),
    NEL("", ""),
    NO_VARY_SEARCH("", ""),
    OBSERVE_BROWSING_TOPICS("", ""),
    ORIGIN("", ""),
    ORIGIN_AGENT_CLUSTER("", ""),
    PERMISSIONS_POLICY("", ""),
    PRAGMA("", ""),
    PRIORITY("", ""),
    PROXY_AUTHENTICATE("", ""),
    PROXY_AUTHORIZATION("", ""),
    RANGE("", ""),
    REFERER("", ""),
    REFERRER_POLICY("", ""),
    REPORT_TO("", ""),
    REPORTING_ENDPOINTS("", ""),
    REPR_DIGEST("", ""),
    RETRY_AFTER("", ""),
    RTT("", ""),
    SAVE_DATA("", ""),
    SEC_BROWSING_TOPICS("", ""),
    SEC_CH_PREFERS_COLOR_SCHEME("", ""),
    SEC_CH_PREFERS_REDUCED_MOTION("", ""),
    SEC_CH_PREFERS_REDUCED_TRANSPARENCY("", ""),
    SEC_CH_UA("", ""),
    SEC_CH_UA_ARCH("", ""),
    SEC_CH_UA_BITNESS("", ""),
    SEC_CH_UA_FULL_VERSION("", ""),
    SEC_CH_UA_FULL_VERSION_LIST("", ""),
    SEC_CH_UA_MOBILE("", ""),
    SEC_CH_UA_MODEL("", ""),
    SEC_CH_UA_PLATFORM("", ""),
    SEC_CH_UA_PLATFORM_VERSION("", ""),
    SEC_FETCH_DEST("", ""),
    SEC_FETCH_MODE("", ""),
    SEC_FETCH_SITE("", ""),
    SEC_FETCH_USER("", ""),
    SEC_GPC("", ""),
    SEC_PURPOSE("", ""),
    SEC_WEBSOCKET_ACCEPT("", ""),
    SEC_WEBSOCKET_KEY("", ""),
    SERVER("", ""),
    SERVER_TIMING("", ""),
    SERVICE_WORKER_NAVIGATION_PRELOAD("", ""),
    SET_COOKIE("", ""),
    SET_LOGIN("", ""),
    SOURCEMAP("", ""),
    SPECULATION_RULES("", ""),
    STRICT_TRANSPORT_SECURITY("", ""),
    SUPPORTS_LOADING_MODE("", ""),
    TE("", ""),
    TIMING_ALLOW_ORIGIN("", ""),
    TK("", ""),
    TRAILER("", ""),
    TRANSFER_ENCODING("", ""),
    UPGRADE("", ""),
    UPGRADE_INSECURE_REQUESTS("", ""),
    USER_AGENT("", ""),
    VARY("", ""),
    VIA("", ""),
    VIEWPORT_WIDTH("", ""),
    WANT_CONTENT_DIGEST("", ""),
    WANT_DIGEST("", ""),
    WANT_REPR_DIGEST("", ""),
    WARNING("", ""),
    WIDTH("", ""),
    WWW_AUTHENTICATE("", ""),
    X_CONTENT_TYPE_OPTIONS("", ""),
    X_DNS_PREFETCH_CONTROL("", ""),
    X_FORWARDED_FOR("", ""),
    X_FORWARDED_HOST("", ""),
    X_FORWARDED_PROTO("", ""),
    X_FRAME_OPTIONS("", ""),
    X_XSS_PROTECTION("", "");

    private final String header;
    private final String defaultValue;

    HTTPHeader(String header, String defaultValue) {
        this.header = header;
        this.defaultValue = defaultValue;
    }

    public String getHeader() {
        return header;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
