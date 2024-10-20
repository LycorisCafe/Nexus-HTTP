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
    private final int connectionTimeout;
    private final ThreadType threadType;

    private final String basePackage;
    private final String tempDirectory;
    private final String staticFilesDirectory;
    private final String databaseLocation;

    private final int maxIncomingConnections;
    private final int pipelineParallelProcesses;
    private final int maxContentLength;

    private final boolean debugEnabled;

    public HttpServerConfiguration(HttpServerConfigurationBuilder builder) {
        port = builder.port;
        backlog = builder.backlog;
        inetAddress = builder.inetAddress;
        connectionTimeout = builder.connectionTimeout;
        threadType = builder.threadType;
        basePackage = builder.basePackage;
        tempDirectory = builder.tempDirectory;
        staticFilesDirectory = builder.staticFilesDirectory;
        databaseLocation = builder.databaseLocation;
        maxIncomingConnections = builder.maxIncomingConnections;
        pipelineParallelProcesses = builder.pipelineParallelProcesses;
        maxContentLength = builder.maxContentLength;
        debugEnabled = builder.debugEnabled;
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

    public int getConnectionTimeout() {
        return connectionTimeout;
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

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static HttpServerConfigurationBuilder builder(String basePackage) {
        return new HttpServerConfigurationBuilder(basePackage);
    }

    public static final class HttpServerConfigurationBuilder {
        private int port = 0;
        private int backlog = 0;
        private InetAddress inetAddress = null;
        private int connectionTimeout = 60_000;
        private ThreadType threadType = ThreadType.VIRTUAL;

        private final String basePackage;
        private String tempDirectory = "NexusTemp";
        private String staticFilesDirectory = "NexusStatics";
        private String databaseLocation = null;

        private int maxIncomingConnections = 100;
        private int pipelineParallelProcesses = 1;
        private int maxContentLength = 5_242_880;

        private boolean debugEnabled;

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

        public HttpServerConfigurationBuilder inetAddress(InetAddress inetAddress)
                throws HttpServerConfigurationException {
            if (inetAddress == null) {
                throw new HttpServerConfigurationException("inet address cannot be null");
            }
            this.inetAddress = inetAddress;
            return this;
        }

        public HttpServerConfigurationBuilder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public HttpServerConfigurationBuilder threadType(ThreadType threadType)
                throws HttpServerConfigurationException {
            if (threadType == null) {
                throw new HttpServerConfigurationException("thread type cannot be null");
            }
            this.threadType = threadType;
            return this;
        }

        public HttpServerConfigurationBuilder tempDirectory(String tempDirectory) {
            if (tempDirectory != null) {
                this.tempDirectory = tempDirectory;
            }
            return this;
        }

        public HttpServerConfigurationBuilder staticFilesDirectory(String staticFilesDirectory) {
            if (staticFilesDirectory != null) {
                this.staticFilesDirectory = staticFilesDirectory;
            }
            return this;
        }

        public HttpServerConfigurationBuilder databaseLocation(String databaseLocation) {
            if (databaseLocation != null) {
                this.databaseLocation = databaseLocation;
            }
            return this;
        }

        public HttpServerConfigurationBuilder maxIncomingConnections(int maxIncomingConnections)
                throws HttpServerConfigurationException {
            if (maxIncomingConnections < 1) {
                throw new HttpServerConfigurationException("max incoming connection count cannot be less than 1");
            }
            this.maxIncomingConnections = maxIncomingConnections;
            return this;
        }

        public HttpServerConfigurationBuilder pipelineParallelProcesses(int pipelineParallelProcesses)
                throws HttpServerConfigurationException {
            if (pipelineParallelProcesses < 1) {
                throw new HttpServerConfigurationException("pipeline processes count cannot be less than 1");
            }
            this.pipelineParallelProcesses = pipelineParallelProcesses;
            return this;
        }

        public HttpServerConfigurationBuilder maxContentLength(int maxContentLength)
                throws HttpServerConfigurationException {
            if (maxContentLength < 1) {
                throw new HttpServerConfigurationException("max content length cannot be less than 1 (MB)");
            }
            this.maxContentLength = maxContentLength;
            return this;
        }

        public HttpServerConfigurationBuilder enableDebug() {
            debugEnabled = true;
            return this;
        }

        public HttpServerConfiguration build() {
            return new HttpServerConfiguration(this);
        }
    }
}
