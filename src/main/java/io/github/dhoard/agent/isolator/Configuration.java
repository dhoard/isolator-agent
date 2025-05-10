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

import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

/**
 * Configuration class for loading Java agent configurations.
 *
 * <p>This class provides a method to parse a YAML configuration file and convert it into a list of
 * {@link JavaAgent} objects.
 */
public class Configuration {

    /**
     * Constants for YAML keys.
     */
    private static final String JAVA_AGENTS = "javaAgents";

    private static final String JAR_PATH = "jarPath";
    private static final String CLASS_NAME = "className";
    private static final String OPTIONS = "options";
    private static final String ENABLED = "enabled";

    /**
     * Private constructor to prevent instantiation.
     */
    private Configuration() {
        // INTENTIONALLY EMPTY
    }

    /**
     * Parses a YAML configuration file and converts it into a list of {@link JavaAgent} objects.
     *
     * @param reader the reader for the YAML configuration file
     * @return a list of {@link JavaAgent} objects
     */
    public static List<JavaAgent> parse(Reader reader) {
        List<JavaAgent> javaAgents = new ArrayList<>();

        // Create a new LoadSettings instance
        LoadSettings settings = LoadSettings.builder().build();

        // Create a new Load instance with the settings
        Load load = new Load(settings);

        // Load the YAML content from the reader
        Object rootObject = load.loadFromReader(reader);

        // Convert the root object to a map
        Map<?, ?> javaAgentsMap = toMap(rootObject, "javaAgents must be a map");

        // Convert the javaAgentsMap to a list
        List<?> javaAgentsList =
                toList(javaAgentsMap.get(JAVA_AGENTS), "javaAgents must contain a list of java agents");

        for (Object javaAgentObject : javaAgentsList) {
            // Convert each javaAgentObject to a map
            Map<?, ?> javaAgentMap = toMap(javaAgentObject, "javaAgent must be a map");

            // Extract the values from the map
            String jarPath = toNonEmptyString(javaAgentMap.get(JAR_PATH), "jarPath must be a non-empty string");
            String className = toNonEmptyString(javaAgentMap.get(CLASS_NAME), "className must be a non-empty string");
            String options = toString(javaAgentMap.get(OPTIONS), "options must be a string");
            boolean isEnabled = toBoolean(javaAgentMap.get(ENABLED), true, "enabled must be a boolean");

            // If the Java agent is enabled, create a new JavaAgent object and add it to the list
            if (isEnabled) {
                javaAgents.add(new JavaAgent(Paths.get(jarPath), className, options));
            }
        }

        return javaAgents;
    }

    /**
     * Converts an object to a list.
     *
     * @param object the object to convert
     * @param errorMessage the error message to throw if the object is not a list
     * @return the list
     */
    private static List<?> toList(Object object, String errorMessage) {
        if (!(object instanceof List)) {
            throw new ConfigurationException(errorMessage);
        }

        return (List<?>) object;
    }

    /**
     * Converts an object to a map.
     *
     * @param object the object to convert
     * @param errorMessage the error message to throw if the object is not a map
     * @return the map
     */
    private static Map<?, ?> toMap(Object object, String errorMessage) {
        if (!(object instanceof Map)) {
            throw new ConfigurationException(errorMessage);
        }

        return (Map<?, ?>) object;
    }

    /**
     * Converts an object to a boolean value.
     *
     * @param object the object to convert
     * @param defaultValue the default value to return if the object is null
     * @param errorMessage the error message to throw if the object is not a boolean
     * @return the boolean value
     */
    private static boolean toBoolean(Object object, boolean defaultValue, String errorMessage) {
        if (object == null) {
            return defaultValue;
        }

        if (object instanceof Boolean) {
            return (Boolean) object;
        }

        throw new ConfigurationException(errorMessage);
    }

    /**
     * Converts an object to a string and trims it.
     *
     * @param object the object to convert
     * @param errorMessage the error message to throw if the object is not a string
     * @return the trimmed string
     */
    private static String toString(Object object, String errorMessage) {
        if (!(object instanceof String)) {
            throw new ConfigurationException(errorMessage);
        }

        return ((String) object).trim();
    }

    /**
     * Converts an object to a non-empty string.
     *
     * @param object the object to convert
     * @param errorMessage the error message to throw if the object is null or empty
     * @return the non-empty string
     */
    private static String toNonEmptyString(Object object, String errorMessage) {
        if (object == null) {
            throw new ConfigurationException(errorMessage);
        }

        if (!(object instanceof String)) {
            throw new ConfigurationException(errorMessage);
        }

        String string = ((String) object).trim();

        if (string.isEmpty()) {
            throw new ConfigurationException(errorMessage);
        }

        return string;
    }
}
