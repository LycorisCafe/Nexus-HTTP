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

package io.github.lycoriscafe.nexus.http.helper.util;

import org.slf4j.spi.LoggingEventBuilder;

/**
 * Format the SLF4j log output by adding {@code NEXUS-HTTP :: } prefix.
 * @see #log(LoggingEventBuilder, String)
 * @since v1.0.0
 */
public final class LogFormatter {
    /**
     * Format the output log by adding {@code NEXUS-HTTP :: } prefix.
     * @param loggingEventBuilder SLF4j logger event builder. Like {@code atDebug()}.
     * @param message Message to log
     * @see LogFormatter
     * @since v1.0.0
     */
    public static void log(final LoggingEventBuilder loggingEventBuilder,
                           final String message) {
        loggingEventBuilder.log("NEXUS-HTTP :: " + message);
    }
}
