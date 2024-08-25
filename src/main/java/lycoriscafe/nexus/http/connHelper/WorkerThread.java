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

package lycoriscafe.nexus.http.connHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WorkerThread implements Runnable {
    private final Socket SOCKET;
    private final InputStream READER;
    private final OutputStream WRITER;

    public WorkerThread(final Socket socket) throws IOException {
        this.SOCKET = socket;
        READER = SOCKET.getInputStream();
        WRITER = SOCKET.getOutputStream();
    }

    @Override
    public void run() {
        try {
            int read = 0;
            while (read != -1) {
                read = READER.read();
                System.out.println(READER.read());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
