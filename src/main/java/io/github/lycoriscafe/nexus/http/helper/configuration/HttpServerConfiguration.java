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

import java.net.InetAddress;

public final class HttpServerConfiguration {
    private final int port;
    private final int backlog;
    private final InetAddress inetAddress;
    private final ThreadType threadType;

    private final String basePackage;
    private final String tempDirectory;
    private final String staticFilesDirectory;
    private final String databaseLocation;

    private final int maxIncomingConnections;
    private final int pipelineParallelProcesses;
    private final int maxContentLength;

    public HttpServerConfiguration(HttpServerConfigurationBuilder builder) {
        port = builder.port;
        backlog = builder.backlog;
        inetAddress = builder.inetAddress;
        threadType = builder.threadType;
        basePackage = builder.basePackage;
        tempDirectory = builder.tempDirectory;
        staticFilesDirectory = builder.staticFilesDirectory;
        databaseLocation = builder.databaseLocation;
        maxIncomingConnections = builder.maxIncomingConnections;
        pipelineParallelProcesses = builder.pipelineParallelProcesses;
        maxContentLength = builder.maxContentLength;
    }

    public int getPort() {
        return port;
    }

    public int getBacklog() {
        return backlog;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public ThreadType getThreadType() {
        return threadType;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }

    public String getStaticFilesDirectory() {
        return staticFilesDirectory;
    }

    public String getDatabaseLocation() {
        return databaseLocation;
    }

    public int getMaxIncomingConnections() {
        return maxIncomingConnections;
    }

    public int getPipelineParallelProcesses() {
        return pipelineParallelProcesses;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public static final class HttpServerConfigurationBuilder {
        private int port = 0;
        private int backlog = 0;
        private InetAddress inetAddress = null;
        private ThreadType threadType = ThreadType.VIRTUAL;

        private final String basePackage;
        private String tempDirectory = "NexusTemp";
        private String staticFilesDirectory = "NexusStatics";
        private String databaseLocation = null;

        private int maxIncomingConnections = 100;
        private int pipelineParallelProcesses = 0;
        private int maxContentLength = 5_242_880;

        public HttpServerConfigurationBuilder(String basePackage) {
            this.basePackage = basePackage;
        }

        public HttpServerConfigurationBuilder port(int port) {
            this.port = port;
            return this;
        }

        public HttpServerConfigurationBuilder backlog(int backlog) {
            this.backlog = backlog;
            return this;
        }

        public HttpServerConfigurationBuilder inetAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
            return this;
        }

        public HttpServerConfigurationBuilder threadType(ThreadType threadType) {
            this.threadType = threadType;
            return this;
        }

        public HttpServerConfigurationBuilder tempDirectory(String tempDirectory) {
            this.tempDirectory = tempDirectory;
            return this;
        }

        public HttpServerConfigurationBuilder staticFilesDirectory(String staticFilesDirectory) {
            this.staticFilesDirectory = staticFilesDirectory;
            return this;
        }

        public HttpServerConfigurationBuilder databaseLocation(String databaseLocation) {
            this.databaseLocation = databaseLocation;
            return this;
        }

        public HttpServerConfigurationBuilder maxIncomingConnections(int maxIncomingConnections) {
            this.maxIncomingConnections = maxIncomingConnections;
            return this;
        }

        public HttpServerConfigurationBuilder pipelineParallelProcesses(int pipelineParallelProcesses) {
            this.pipelineParallelProcesses = pipelineParallelProcesses;
            return this;
        }

        public HttpServerConfigurationBuilder maxContentLength(int maxContentLength) {
            this.maxContentLength = maxContentLength;
            return this;
        }

        public HttpServerConfiguration build() {
            return new HttpServerConfiguration(this);
        }
    }
}
