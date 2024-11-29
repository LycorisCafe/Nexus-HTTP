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

import java.util.List;

public class Content {
    private final String contentType;
    private final long contentLength;
    private final List<TransferEncoding> transferEncodings;
    private final List<ContentEncoding> contentEncodings;
    private Object data;

    public Content(String contentType,
                   long contentLength,
                   List<TransferEncoding> transferEncodings,
                   List<ContentEncoding> contentEncodings,
                   Object data) {
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.transferEncodings = transferEncodings;
        this.contentEncodings = contentEncodings;
        this.data = data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public List<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public List<ContentEncoding> getContentEncodings() {
        return contentEncodings;
    }

    public Object getData() {
        return data;
    }
}
