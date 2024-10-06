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

package io.github.lycoriscafe.nexus.http.helper.models;

import io.github.lycoriscafe.nexus.http.core.requestMethods.HttpRequestMethod;

public final class ReqEndpoint extends ReqMaster {
    private final String className;
    private final String methodName;
    private final String statusAnnotation;
    private final String statusAnnotationValue;

    public ReqEndpoint(String endpoint,
                       HttpRequestMethod reqMethod,
                       String className,
                       String methodName,
                       String statusAnnotation,
                       String statusAnnotationValue) {
        super(endpoint, reqMethod);
        this.className = className;
        this.methodName = methodName;
        this.statusAnnotation = statusAnnotation;
        this.statusAnnotationValue = statusAnnotationValue;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getStatusAnnotation() {
        return statusAnnotation;
    }

    public String getStatusAnnotationValue() {
        return statusAnnotationValue;
    }
}
