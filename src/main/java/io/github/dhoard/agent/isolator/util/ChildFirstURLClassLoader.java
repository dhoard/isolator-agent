/*
 * Copyright (C) 2025-present Doug Hoard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dhoard.agent.isolator.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A custom URLClassLoader that loads classes from the child classloader first.
 */
public class ChildFirstURLClassLoader extends URLClassLoader {

    private final ClassLoader system;

    /**
     * Constructor for ChildFirstURLClassLoader.
     *
     * @param urls URLs to load classes from
     * @param parent Parent classloader
     */
    public ChildFirstURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);

        system = getSystemClassLoader();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // Check if already loaded
            Class<?> clazz = findLoadedClass(name);

            if (clazz == null) {
                try {
                    // Load from this classloader first
                    clazz = findClass(name);
                } catch (ClassNotFoundException e) {
                    // If not found, delegate to system or parent
                    try {
                        clazz = system.loadClass(name);
                    } catch (ClassNotFoundException ex) {
                        clazz = super.loadClass(name, resolve);
                    }
                }
            }

            if (resolve) {
                resolveClass(clazz);
            }

            return clazz;
        }
    }
}
