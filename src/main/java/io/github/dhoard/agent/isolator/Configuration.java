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

        if (rootObject instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) rootObject;
            Object javaAgentsObject = map.get(JAVA_AGENTS);

            if (javaAgentsObject instanceof List) {
                List<?> javaAgentsList = (List<?>) javaAgentsObject;

                for (Object javaAgentObject : javaAgentsList) {
                    if (javaAgentObject instanceof Map) {
                        Map<?, ?> agentMap = (Map<?, ?>) javaAgentObject;

                        String jarPath = (String) agentMap.get(JAR_PATH);
                        String className = (String) agentMap.get(CLASS_NAME);
                        String options = (String) agentMap.get(OPTIONS);

                        boolean isEnabled = true;
                        Object enabledObject = agentMap.get(ENABLED);

                        if (enabledObject instanceof Boolean) {
                            isEnabled = (Boolean) enabledObject;
                        } else if (enabledObject instanceof String) {
                            isEnabled = parseBoolean((String) enabledObject, true);
                        }

                        if (isEnabled) {
                            javaAgents.add(new JavaAgent(Paths.get(jarPath), className, options));
                        }
                    }
                }
            }
        }

        return javaAgents;
    }

    private static boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        value = value.trim();

        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1");
    }
}
