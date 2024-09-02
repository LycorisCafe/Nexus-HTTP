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

package io.github.lycoriscafe.nexus.http.connHelper.methodProcessors;

import io.github.lycoriscafe.nexus.http.connHelper.RequestHandler;
import io.github.lycoriscafe.nexus.http.httpHelper.manager.HTTPResponse;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class CommonProcessor {
    public static HTTPResponse<?> processErrors(final HTTPResponse<?> RESPONSE) {
        // TODO process headers & body
        return RESPONSE;
    }

    public void skipContent(final RequestHandler REQ_HANDLER,
                            long bytes) throws IOException {
        REQ_HANDLER.getInputStream().skipNBytes(bytes);
    }

    public static String getServerTime() {
        return DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                .withZone(ZoneId.of("GMT")).format(ZonedDateTime.now());
    }
}
