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

import io.github.lycoriscafe.nexus.http.helper.util.DataList;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public final class Content<T> {
    private final String contentType;
    private long contentLength;
    private String downloadName;
    private List<TransferEncoding> transferEncodings;
    private List<ContentEncoding> contentEncodings;
    private T data;

    public Content(final String contentType,
                   final T data) throws ContentException {
        if (!((data instanceof Path) || (data instanceof byte[]) || (data instanceof InputStream) ||
                (data instanceof String))) {
            throw new ContentException("invalid data type");
        }

        this.contentType = contentType;
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public Content<T> setDownloadName(final String downloadName) {
        this.downloadName = downloadName;
        return this;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public Content<T> addTransferEncoding(final TransferEncoding transferEncoding) {
        if (transferEncodings == null) {
            transferEncodings = new DataList<>();
        }
        transferEncodings.add(transferEncoding);
        return this;
    }

    public List<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public Content<T> addContentEncoding(final ContentEncoding contentEncoding) {
        if (contentEncodings == null) {
            contentEncodings = new DataList<>();
        }
        contentEncodings.add(contentEncoding);
        return this;
    }

    public List<ContentEncoding> getContentEncodings() {
        return contentEncodings == null ? null : contentEncodings.stream().toList();
    }

    Content<T> setData(final T data) {
        this.data = data;
        return this;
    }

    public T getData() {
        return data;
    }

    public static class ReadOperations {

    }

    public static class WriteOperations {

    }
}
