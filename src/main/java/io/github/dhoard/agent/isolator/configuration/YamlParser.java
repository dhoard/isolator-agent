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

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.yaml.snakeyaml.Yaml;

/**
 * Utility class for parsing YAML configuration files for Java agents.
 * <p>
 * This class provides methods to parse a YAML file and convert it into a list of JavaAgent objects.
 */
public class YamlParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private YamlParser() {
        // INTENTIONALLY BLANK
    }

    /**
     * Parses a YAML file and returns a list of JavaAgent objects.
     *
     * @param path Path to the YAML file
     * @return List of JavaAgent objects
     * @throws IOException If an I/O error occurs while reading the file
     */
    public static List<JavaAgent> parse(String path) throws IOException {
        return parse(Paths.get(path));
    }

    /**
     * Parses a YAML file and returns a list of JavaAgent objects.
     *
     * @param path Path to the YAML file
     * @return List of JavaAgent objects
     * @throws IOException If an I/O error occurs while reading the file
     */
    public static List<JavaAgent> parse(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return parse(reader);
        }
    }

    /**
     * Parses a YAML file and returns a list of JavaAgent objects.
     *
     * @param reader Reader for the YAML file
     * @return List of JavaAgent objects
     * @throws IOException If an I/O error occurs while reading the file
     */
    public static List<JavaAgent> parse(Reader reader) throws IOException {
        // Create a new Yaml instance
        Yaml yaml = new Yaml();

        // Load the YAML file into a JavaAgentList object
        JavaAgentList javaAgentList = yaml.loadAs(reader, JavaAgentList.class);

        // Check if the JavaAgentList is not null and contains Java agents
        return javaAgentList != null && javaAgentList.getJavaAgents() != null
                ? javaAgentList.getJavaAgents()
                : Collections.emptyList();
    }
}
