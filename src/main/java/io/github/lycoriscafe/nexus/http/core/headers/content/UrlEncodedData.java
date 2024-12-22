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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Content type {@code application/x-www-form-urlencoded} for <b>incoming content</b>. If request has header
 * {@code Content-Type: application/x-www-form-urlencoded}, then the {@code Content.getData()} type should cast to {@code UrlEncodedData}.
 * <pre>
 *     {@code
 *     // 'request' is from endpoint parameter (HttpPostRequest, ...)
 *     var content = request.getContent();
 *     if (content.getContentType().equals("application/x-www-form-urlencoded")) {
 *         UrlEncodedData encodedData = (UrlEncodedData) content.getData();
 *         for (String key : encodedData.keySet()) {
 *              // ...
 *         }
 *     }
 *     }
 * </pre>
 *
 * @apiNote Since {@code UrlEncodedData} extends {@code HasMap<String, String>}, API users can treat this as a {@code Map<String, String>}.
 * @see Content
 * @see Content#getData()
 * @since v1.0.0
 */
public final class UrlEncodedData extends HashMap<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(UrlEncodedData.class);

    /**
     * Process incoming {@code application/x-www-form-urlencoded} content type request.
     *
     * @param requestId       {@code HttpRequest} id
     * @param requestConsumer {@code RequestConsumer} bound to the {@code HttpRequest}
     * @param contentLength   {@code Content-Length}
     * @param chunked         {@code Transfer-Encoding} chunked?
     * @param gzipped         {@code Content-Encoding} gzipped?
     * @return New instance of {@code Content}
     * @throws IOException Error while reading data from socket input stream
     * @apiNote This method is public but not useful for the API users. Only used for in-API tasks.
     * @see io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpReq.HttpRequest HttpRequest
     * @see RequestConsumer
     * @see Content
     * @since v1.0.0
     */
    public static Content process(final long requestId,
                                  final RequestConsumer requestConsumer,
                                  final Integer contentLength,
                                  final boolean chunked,
                                  final boolean gzipped) throws IOException {
        if (chunked) {
            logger.atDebug().log("Drop request - RequestId:" + requestId + ", StatusCode:" + HttpStatusCode.BAD_REQUEST);
            requestConsumer.dropConnection(requestId, HttpStatusCode.BAD_REQUEST, "transfer encoding not supported");
            return null;
        }

        Content content = Content.ReadOperations.process(requestId, requestConsumer, "application/x-www-form-urlencoded", contentLength, false, gzipped);
        if (content == null) return null;

        String[] data = (new String((byte[]) content.getData(), StandardCharsets.UTF_8)).split("&", 0);
        UrlEncodedData urlEncodedData = new UrlEncodedData();
        for (String s : data) {
            String[] keyVal = s.split("=", 2);
            urlEncodedData.put(URLDecoder.decode(keyVal[0], StandardCharsets.UTF_8), URLDecoder.decode(keyVal[1], StandardCharsets.UTF_8));
        }

        return new Content("application/x-www-form-urlencoded", urlEncodedData);
    }
}
