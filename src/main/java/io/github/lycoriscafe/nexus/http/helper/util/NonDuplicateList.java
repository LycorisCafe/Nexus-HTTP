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

package io.github.lycoriscafe.nexus.http.helper.util;

import java.util.ArrayList;

/**
 * {@code ArrayList<E>} with no duplicates!
 *
 * @param <E> Generic type element
 * @see ArrayList
 * @since v1.0.0
 */
public final class NonDuplicateList<E> extends ArrayList<E> {
    /**
     * Check for element presence and if absent add to the {@code ArrayList}.
     *
     * @param e element whose presence in this collection is to be ensured
     * @return If the element added, {@code true} else {@code false}
     * @see ArrayList#add(Object)
     * @see NonDuplicateList
     * @since v1.0.0
     */
    @Override
    public boolean add(final E e) {
        if (!contains(e)) {
            return super.add(e);
        }
        return false;
    }
}
