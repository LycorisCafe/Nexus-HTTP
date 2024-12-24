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

package io.github.lycoriscafe.nexus.http.core.statusCodes;

import io.github.lycoriscafe.nexus.http.core.headers.auth.Authenticated;
import io.github.lycoriscafe.nexus.http.core.statusCodes.annotations.*;

/**
 * HTTP status codes.
 *
 * @see <a href="https://datatracker.ietf.org/doc/rfc9110">HTTP Semantics (rfc 9110)</a>
 * @since v1.0.0
 */
public enum HttpStatusCode {
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    CONTINUE("100 Continue"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    SWITCHING_PROTOCOLS("101 Switching Protocols"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PROCESSING("102 Processing"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    EARLY_HINTS("103 Early Hints"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    OK("200 OK"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    CREATED("201 Created"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    ACCEPTED("202 Accepted"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NON_AUTHORITATIVE_INFORMATION("203 Non-Authoritative Information"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NO_CONTENT("204 No Content"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    RESET_CONTENT("205 Reset Content"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PARTIAL_CONTENT("206 Partial Content"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    MULTI_STATUS("207 Multi-Status"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    ALREADY_REPORTED("208 Already Reported"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    IM_USED("209 IM Used"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    MULTIPLE_CHOICES("300 Multiple Choices"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    MOVED_PERMANENTLY("301 Moved Permanently"),
    /**
     * @see MovedPermanently
     * @see HttpStatusCode
     * @since v1.0.0
     */
    FOUND("302 Found"),
    /**
     * @see Found
     * @see HttpStatusCode
     * @since v1.0.0
     */
    SEE_OTHER("303 See Other"),
    /**
     * @see SeeOther
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NOT_MODIFIED("304 Not Modified"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    TEMPORARY_REDIRECT("307 Temporary Redirect"),
    /**
     * @see TemporaryRedirect
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PERMANENT_REDIRECT("308 Permanent Redirect"),
    /**
     * @see PermanentRedirect
     * @see HttpStatusCode
     * @since v1.0.0
     */
    BAD_REQUEST("400 Bad Request"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    UNAUTHORIZED("401 Unauthorized"),
    /**
     * @see Authenticated
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PAYMENT_REQUIRED("402 Payment Required"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    FORBIDDEN("403 Forbidden"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NOT_FOUND("404 Not Found"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NOT_ACCEPTABLE("406 Not Acceptable"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PROXY_AUTHENTICATION_REQUIRED("407 Proxy Authentication Required"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    REQUEST_TIMEOUT("408 Request Timeout"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    CONFLICT("409 Conflict"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    GONE("410 Gone"),
    /**
     * @see Gone
     * @see HttpStatusCode
     * @since v1.0.0
     */
    LENGTH_REQUIRED("411 Length Required"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PRECONDITION_FAILED("412 Precondition Failed"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    CONTENT_TOO_LARGE("413 Content Too Large"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    URI_TOO_LONG("414 URI Too Long"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    UNSUPPORTED_MEDIA_TYPE("415 Unsupported Media Type"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    RANGE_NOT_SATISFIABLE("416 Range Not Satisfiable"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    EXPECTATION_FAILED("417 Expectation Failed"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    I_M_A_TEAPOT("418 I'm a Teapot"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    MISDIRECTED_REQUEST("421 Misdirected Request"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    UNPROCESSABLE_CONTENT("422 Unprocessable Content"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    LOCKED("423 Locked"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    FAILED_DEPENDENCY("424 Failed Dependency"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    TOO_EARLY("425 Too Early"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    UPGRADE_REQUIRED("426 Upgrade Required"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    PRECONDITION_REQUIRED("428 Precondition Required"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    TOO_MANY_REQUESTS("429 Too Many Requests"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE("431 Request Header Fields Too Large"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    UNAVAILABLE_FOR_LEGAL_REASONS("451 Unavailable For Legal Reasons"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NOT_IMPLEMENTED("501 Not Implemented"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    BAD_GATEWAY("502 Bad Gateway"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    SERVICE_UNAVAILABLE("503 Service Unavailable"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    GATEWAY_TIMEOUT("504 Gateway Timeout"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    HTTP_VERSION_NOT_SUPPORTED("505 HTTP Version Not Supported"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    VARIANT_ALSO_NEGOTIATES("506 Variant Also Negotiates"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    INSUFFICIENT_STORAGE("507 Insufficient Storage"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    LOOP_DETECTED("508 Loop Detected"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NOT_EXTENDED("510 Not Extended"),
    /**
     * @see HttpStatusCode
     * @since v1.0.0
     */
    NETWORK_AUTHENTICATION_REQUIRED("511 Network Authentication Required");

    private final String statusCode;

    /**
     * String value to set in header
     *
     * @param statusCode String value
     * @see HttpStatusCode
     * @since v1.0.0
     */
    HttpStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Get formatted HTTP status code to set to the response.
     *
     * @return Formatted HTTP status code
     * @see HttpStatusCode
     * @since v1.0.0
     */
    public String getStatusCode() {
        return statusCode;
    }
}
