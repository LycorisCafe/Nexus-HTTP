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

package io.github.lycoriscafe.nexus.http.helper.models;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;
import io.github.lycoriscafe.nexus.http.helper.Database;
import io.github.lycoriscafe.nexus.http.helper.scanners.FileScanner;

/**
 * Static file endpoint model for communicate endpoint data to/from endpoint methods to/from the database.
 *
 * @see FileScanner
 * @see ReqMaster
 * @see Database
 * @since v1.0.0
 */
public final class ReqFile extends ReqMaster {
    private final String lastModified;
    private final String eTag;

    /**
     * Create instance of {@code ReqFile}.
     *
     * @param location      Static file location as HTTP URI
     * @param authenticated Is the endpoint authenticated?
     * @param lastModified  Last modified date in HTTP date format
     * @param eTag          Calculated MD5 E-Tag
     * @see ReqFile
     * @since v1.0.0
     */
    public ReqFile(final String location,
                   final boolean authenticated,
                   final String lastModified,
                   final String eTag) {
        super(location, HttpRequestMethod.GET, authenticated);
        this.lastModified = lastModified;
        this.eTag = eTag;
    }

    /**
     * Get provided last modified date in HTTP date format.
     *
     * @return Last modified date
     * @see ReqFile
     * @since v1.0.0
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * Get provided calculated MD5 E-Tag
     *
     * @return MD5 E-Tag
     * @see ReqFile
     * @since v1.0.0
     */
    public String getETag() {
        return eTag;
    }
}
