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

package io.github.lycoriscafe.nexus.http.connHelper;

import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;

import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class RequestProcessor {
    private static final int reqId = 0;

    private final HTTPServerConfiguration CONFIGURATION;
    private final Socket SOCKET;
    private final ArrayList<String> HEADERS;
    private final Connection DATABASE;
    private final Map<String, ArrayList<String>> HEADERS_MAP;

    RequestProcessor(final HTTPServerConfiguration CONFIGURATION,
                     final Socket SOCKET,
                     final ArrayList<String> HEADERS,
                     final Connection DATABASE) {
        this.CONFIGURATION = CONFIGURATION;
        this.SOCKET = SOCKET;
        this.HEADERS = HEADERS;
        this.DATABASE = DATABASE;
        HEADERS_MAP = new HashMap<>();
    }

    void process() {
        for (int i = 1; i < HEADERS.size(); i++) {
            String[] h = HEADERS.get(i).split(":");
            String values = HEADERS.get(i).replace(h[0], "");
            ArrayList<String> valuesList = new ArrayList<>();
            for (String value : values.split(",")) {

            }
        }

    }
}
