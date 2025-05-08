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

package io.github.dhoard.agent.isolator.configuration;

/**
 * Represents a Java agent configuration.
 * <p>
 * This class contains the information needed to load and run a Java agent, including the jar file,
 * the class name, and any arguments to be passed to the agent.
 */
public class JavaAgent {

    private String jarPath;
    private String className;
    private String options;

    /**
     * Default constructor for JavaAgent.
     * <p>
     * This constructor is intentionally empty and is provided for serialization purposes.
     */
    public JavaAgent() {
        // INTENTIONALLY EMPTY
    }

    /**
     * Constructor for JavaAgent.
     *
     * @param jarPath   Path to the jar file
     * @param className Name of the class to be loaded
     * @param options   Options to be passed to the agent
     */
    public JavaAgent(String jarPath, String className, String options) {
        this.jarPath = jarPath;
        this.className = className;
        this.options = options;
    }

    /**
     * Sets the jar file path.
     *
     * @param jarPath Path to the jar file
     */
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    /**
     * Gets the jar path..
     *
     * @return path to the jar file
     */
    public String getJarPath() {
        return jarPath;
    }

    /**
     * Sets the class name.
     *
     * @param className Name of the class to be loaded
     */
    public void setClassName(String className) {
        this.className = className;
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
     * Sets the options to be passed to the agent.
     *
     * @param options Options to be passed to the agent
     */
    public void setOptions(String options) {
        this.options = options;
    }

    /**
     * Gets the options to be passed to the agent.
     *
     * @return options to be passed to the agent
     */
    public String getOptions() {
        return options;
    }
}
