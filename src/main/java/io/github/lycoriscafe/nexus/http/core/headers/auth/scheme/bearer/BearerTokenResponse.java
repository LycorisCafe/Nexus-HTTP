/*
 * Copyright 2024 Lycoris Café
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

package io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;

import io.github.lycoriscafe.nexus.http.engine.ReqResManager.httpRes.HttpResponse;
import io.github.lycoriscafe.nexus.http.engine.RequestConsumer;

/**
 * Working as the parent type for {@code BearerTokenSuccessResponse} and {@code BearerTokenFailResponse}. Useful when implementing Bearer endpoints
 * using {@code @BearerEndpoint}.
 * <pre>
 *     {@code
 *     @BearerEndpint("/token")
 *     public static BearerTokenResponse tokenEndpoint(BearerTokenRequest request) {
 *         // ...
 *         // on success
 *         return new BearerTokenSuccessResponse(...);
 *         // on fail
 *         return new BearerTokenFailResponse(...);
 *     }
 *     }
 * </pre>
 *
 * @see BearerTokenSuccessResponse
 * @see BearerTokenFailResponse
 * @see BearerEndpoint
 * @since v1.0.0
 */
public interface BearerTokenResponse {
    HttpResponse parse(long requestId,
                       RequestConsumer requestConsumer);
}
