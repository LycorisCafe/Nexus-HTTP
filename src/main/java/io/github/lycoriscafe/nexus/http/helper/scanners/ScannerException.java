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

package io.github.lycoriscafe.nexus.http.helper.scanners;

/**
 * Throw exceptions caught while scanning processes.
 *
 * @see EndpointScanner
 * @see FileScanner
 * @since v1.0.0
 */
public class ScannerException extends Exception {
    /**
     * Scanner exception.
     *
     * @param message Exception message to throw
     * @see ScannerException
     * @since v1.0.0
     */
    public ScannerException(String message) {
        super(message);
    }
}
