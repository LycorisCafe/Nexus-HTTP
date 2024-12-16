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

package io.github.lycoriscafe.nexus.http.core.headers.content;

import io.github.lycoriscafe.nexus.http.core.statusCodes.HttpStatusCode;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public final class UrlEncodedData extends HashMap<String, String> {
    public static Content process(final long requestId,
                                  final RequestConsumer requestConsumer,
                                  final Integer contentLength,
                                  final boolean chunked,
                                  final boolean gzipped) throws IOException {
        if (chunked) {
            requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "transfer encoding not supported");
            return null;
        }

        Content content = Content.ReadOperations.process(requestId, requestConsumer, "application/x-www-form-urlencoded", contentLength, false, gzipped);
        if (content == null) return null;

        String[] data = (new String((byte[]) content.getData(), StandardCharsets.UTF_8)).split("&", 0);
        UrlEncodedData urlEncodedData = new UrlEncodedData();
        for (String s : data) {
            String[] keyVal = s.split("=", 2);
            urlEncodedData.put(URLDecoder.decode(keyVal[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8));
        }

        return new Content("application/x-www-form-urlencoded", urlEncodedData);
    }
}
