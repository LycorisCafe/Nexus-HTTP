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

import io.github.lycoriscafe.nexus.http.helper.util.NonDuplicateList;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public final class Content {
    private final String contentType;
    private long contentLength;
    private String downloadName;
    private List<TransferEncoding> transferEncodings;
    private List<ContentEncoding> contentEncodings;
    private Object data;

    Content(final String contentType) {
        this.contentType = contentType;
    }

    private Content(final String contentType,
                    final Object data) {
        this.contentType = contentType;
        this.data = data;
    }

    public Content(final String contentType,
                   final Path data) {
        this(contentType, (Object) data);
    }

    public Content(final String contentType,
                   final byte[] data) {
        this(contentType, (Object) data);
    }

    public Content(final String contentType,
                   final String data) {
        this(contentType, (Object) data);
    }

    public Content(final String contentType,
                   final InputStream data) {
        this(contentType, (Object) data);
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public Content setDownloadName(final String downloadName) {
        this.downloadName = downloadName;
        return this;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public Content addTransferEncoding(final TransferEncoding transferEncoding) {
        if (transferEncodings == null) transferEncodings = new NonDuplicateList<>();
        transferEncodings.add(transferEncoding);
        return this;
    }

    public List<TransferEncoding> getTransferEncodings() {
        return transferEncodings;
    }

    public Content addContentEncoding(final ContentEncoding contentEncoding) {
        if (contentEncodings == null) contentEncodings = new NonDuplicateList<>();
        contentEncodings.add(contentEncoding);
        return this;
    }

    public List<ContentEncoding> getContentEncodings() {
        return contentEncodings;
    }

    Content setData(final Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }

    public static class ReadOperations {

    }

    public static class WriteOperations {

    }
}
