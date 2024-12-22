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

package io.github.lycoriscafe.nexus.http.helper.configuration;

/**
 * In-API database type.
 *
 * @see HttpServerConfiguration#setDatabaseType(DatabaseType)
 * @since v1.0.0
 */
public enum DatabaseType {
    /**
     * In-memory database
     *
     * @see DatabaseType
     * @since v1.0.0
     */
    MEMORY,
    /**
     * Physical database located in temporary directory specified in {@code HttpServerConfiguration}.
     *
     * @see HttpServerConfiguration#setTempDirectory(String)
     * @see DatabaseType
     * @since v1.0.0
     */
    TEMPORARY
}
