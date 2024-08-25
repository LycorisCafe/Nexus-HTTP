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

package lycoriscafe.nexus.http;

import lycoriscafe.nexus.http.connHelper.WorkerThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {
    private final HTTPVersion HTTP_VERSION;
    private final ServerSocket SERVER_SOCKET;
    private final ThreadType THREAD_TYPE;
    private final TempMemoryType TEMP_MEMORY_TYPE;
    private boolean operational;

    public enum HTTPVersion {
        HTTP_1_1
    }

    public enum ThreadType {
        PLATFORM, VIRTUAL
    }

    public enum TempMemoryType {
        PRIMARY, SECONDARY
    }

    public HTTPServer(int port, HTTPVersion httpVersion, ThreadType threadType, TempMemoryType memoryType)
            throws IOException {
        SERVER_SOCKET = new ServerSocket(port);
        HTTP_VERSION = httpVersion;
        THREAD_TYPE = threadType;
        TEMP_MEMORY_TYPE = memoryType;
    }

    public HTTPServer(int port, int backlog, HTTPVersion httpVersion, ThreadType threadType, TempMemoryType memoryType)
            throws IOException {
        SERVER_SOCKET = new ServerSocket(port, backlog);
        HTTP_VERSION = httpVersion;
        THREAD_TYPE = threadType;
        TEMP_MEMORY_TYPE = memoryType;
    }

    public HTTPServer(int port, int backlog, InetAddress host, HTTPVersion httpVersion, ThreadType threadType,
                      TempMemoryType memoryType) throws IOException {
        SERVER_SOCKET = new ServerSocket(port, backlog, host);
        HTTP_VERSION = httpVersion;
        THREAD_TYPE = threadType;
        TEMP_MEMORY_TYPE = memoryType;
    }

    public ServerSocket getSERVER_SOCKET() {
        return SERVER_SOCKET;
    }

    public HTTPVersion getHTTP_VERSION() {
        return HTTP_VERSION;
    }

    public ThreadType getTHREAD_TYPE() {
        return THREAD_TYPE;
    }

    public TempMemoryType getTEMP_MEMORY_TYPE() {
        return TEMP_MEMORY_TYPE;
    }

    public boolean isOperational() {
        return operational;
    }

    public void start()
            throws IOException {
        if (!operational) {
            operational = true;
            while (!SERVER_SOCKET.isClosed()) {
                Socket socket = SERVER_SOCKET.accept();
                if (THREAD_TYPE == ThreadType.PLATFORM) {
                    Thread.ofPlatform().start(new WorkerThread(socket));
                } else {
                    Thread.ofVirtual().start(new WorkerThread(socket));
                }
            }
        } else {
            throw new IllegalStateException("Server is already running!");
        }
    }

    public void stop()
            throws IOException {
        if (operational) {
            operational = false;
            SERVER_SOCKET.close();
        } else {
            throw new IllegalStateException("Server is already stopped!");
        }
    }
}
