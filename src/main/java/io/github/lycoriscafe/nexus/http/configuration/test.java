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

import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.HTTPEndpoint;
import io.github.lycoriscafe.nexus.http.httpHelper.meta.requestMethods.annotations.GET;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public class test {
    public static void main(String[] args) throws ClassNotFoundException {
        Reflections reflections = new Reflections("io");
        Set<Class<?>> modules = reflections.get(SubTypes.of(TypesAnnotated.with(HTTPEndpoint.class)).asClass());
        for (Class<?> module : modules) {
            System.out.println(module.getPackageName());
            Class<?> c = Class.forName(module.getPackageName() + "." + module.getSimpleName());
            System.out.println(Arrays.toString(c.getDeclaredMethods()[0].getName().toCharArray()));
            System.out.println(Arrays.toString(c.getMethods()[0].getAnnotation(GET.class).value().toCharArray()));
        }
    }
}
