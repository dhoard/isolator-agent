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

package io.github.dhoard.agent.isolator;

import java.nio.file.Path;

/**
 * Represents a Java agent configuration.
 * <p>
 * This class contains the information needed to load and run a Java agent, including the jar file,
 * the class name, and any arguments to be passed to the agent.
 */
public class JavaAgent {

    private final Path jarPath;
    private final String className;
    private final String options;

    /**
     * Constructor for JavaAgent.
     *
     * @param jarPath   Path to the jar file
     * @param className Name of the class to be loaded
     * @param options   Options to be passed to the agent
     */
    public JavaAgent(Path jarPath, String className, String options) {
        this.jarPath = jarPath;
        this.className = className;
        this.options = options;
    }

    /**
     * Gets the jar path.
     *
     * @return path to the jar file
     */
    public Path getJarPath() {
        return jarPath;
    }

    /**
     * Gets the class name.
     *
     * @return name of the class to be loaded
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the options.
     *
     * @return options to be passed to the agent
     */
    public String getOptions() {
        return options;
    }
}
