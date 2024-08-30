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

package io.github.lycoriscafe.nexus.http.configuration;

import java.net.InetAddress;

public final class HTTPServerConfiguration {
    private int port = 0;
    private int backlog = 0;
    private InetAddress address = null;
    private ThreadType threadType = ThreadType.PLATFORM;
    private String databaseLocation = null;
    private int maxConnections = 100;
    private boolean httpPipeline = false;
    private int httpPipelineParallelCount = 5;
    private final String basePackage;

    public HTTPServerConfiguration(final Class<?> BASE_CLASS)
            throws IllegalArgumentException, ClassNotFoundException {
        if (BASE_CLASS == null) {
            throw new IllegalArgumentException("Base class cannot be null");
        }

        String pkgName = BASE_CLASS.getPackageName();
        if (pkgName.isEmpty()) {
            throw new IllegalArgumentException("Could not determine package name");
        }

        this.basePackage = pkgName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public HTTPServerConfiguration setPort(final int PORT)
            throws IllegalArgumentException {
        if (PORT < 0) {
            throw new IllegalArgumentException("PORT must be a positive integer or 0");
        }
        port = PORT;
        return this;
    }

    public int getPort() {
        return port;
    }

    public HTTPServerConfiguration setBacklog(final int BACKLOG)
            throws IllegalArgumentException {
        if (BACKLOG < 0) {
            throw new IllegalArgumentException("backlog must be a positive integer or 0");
        }
        backlog = BACKLOG;
        return this;
    }

    public int getBacklog() {
        return backlog;
    }

    public HTTPServerConfiguration setAddress(final InetAddress ADDRESS) {
        address = ADDRESS;
        return this;
    }

    public InetAddress getAddress() {
        return address;
    }

    public HTTPServerConfiguration setThreadType(final ThreadType THREAD_TYPE)
            throws IllegalArgumentException {
        if (THREAD_TYPE == null) {
            throw new IllegalArgumentException("threadType cannot be null");
        }
        threadType = THREAD_TYPE;
        return this;
    }

    public ThreadType getThreadType() {
        return threadType;
    }

    public HTTPServerConfiguration setDatabaseLocation(final String DATABASE_LOCATION) {
        databaseLocation = DATABASE_LOCATION;
        return this;
    }

    public String getDatabaseLocation() {
        return databaseLocation;
    }

    public HTTPServerConfiguration setMaxConnections(final int MAX_CONNECTIONS)
            throws IllegalArgumentException {
        if (MAX_CONNECTIONS < 1) {
            throw new IllegalArgumentException("maxConnections must be a positive integer");
        }
        maxConnections = MAX_CONNECTIONS;
        return this;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public HTTPServerConfiguration setHttpPipeline(final boolean HTTP_PIPELINE) {
        httpPipeline = HTTP_PIPELINE;
        return this;
    }

    public boolean isHttpPipelined() {
        return httpPipeline;
    }

    public HTTPServerConfiguration setHttpPipelineParallelCount(final int HTTP_PIPELINE_PARALLEL_COUNT) {
        httpPipelineParallelCount = HTTP_PIPELINE_PARALLEL_COUNT;
        return this;
    }

    public int getHttpPipelineParallelCount() {
        return httpPipelineParallelCount;
    }
}
