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

package io.github.lycoriscafe.nexus.http.core.headers.csp;

import java.util.List;

public final class ReportingEndpoint {
    private final String name;
    private final String value;

    public ReportingEndpoint(final String name,
                             final String value) {
        if (name == null || value == null) {
            throw new NullPointerException("name/value cannot be null");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static String processOutgoingReportingEndpoints(final List<ReportingEndpoint> reportingEndpoints) {
        if (reportingEndpoints == null || reportingEndpoints.isEmpty()) return "";
        StringBuilder output = new StringBuilder().append("Reporting-Endpoints:");
        for (int i = 0; i < reportingEndpoints.size(); i++) {
            output.append(" ").append(reportingEndpoints.get(i).getName()).append("=").append("\"")
                    .append(reportingEndpoints.get(i).getValue()).append("\"");
            if (i != reportingEndpoints.size() - 1) output.append(",");
        }
        return output.toString();
    }
}
