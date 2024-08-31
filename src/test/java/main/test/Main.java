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

package main.test;

import io.github.lycoriscafe.nexus.http.HTTPServer;
import io.github.lycoriscafe.nexus.http.configuration.HTTPServerConfiguration;
import io.github.lycoriscafe.nexus.http.configuration.ThreadType;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            HTTPServerConfiguration httpServerConfiguration = new HTTPServerConfiguration(Main.class)
                    .setPort(2004)
                    .setThreadType(ThreadType.VIRTUAL)
                    .setBacklog(5)
                    .setDatabaseLocation("");
            HTTPServer httpServer1 = new HTTPServer(httpServerConfiguration);
            httpServer1.start();
            System.out.println(httpServerConfiguration.getBasePackage());
            //            HTTPServer httpServer2 = new HTTPServer(2004, ThreadType.VIRTUAL, MemoryType.PRIMARY, 5);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}