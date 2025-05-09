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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Version class to get the version of the InsolatorAgent
 * <p>
 * This class loads the version from the properties file located in the classpath.
 * </p>
 */
@SuppressWarnings("PMD.EmptyCatchBlock")
public class Version {

    private static final String ISOLATOR_AGENT_PROPERTIES = "/isolator-agent.properties";

    private static final String VERSION_KEY = "version";

    private static final String VERSION_UNKNOWN = "unknown";

    private static final String VERSION;

    static {
        // Set the default version
        String value = VERSION_UNKNOWN;

        // Load the version from the properties file
        try (InputStream inputStream = Version.class.getResourceAsStream(ISOLATOR_AGENT_PROPERTIES)) {
            if (inputStream != null) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    // Load the properties
                    Properties properties = new Properties();
                    properties.load(inputStreamReader);

                    // Get the version
                    value = properties.getProperty(VERSION_KEY).trim();
                }
            }
        } catch (IOException e) {
            // INTENTIONALLY BLANK
        }

        VERSION = value;
    }

    /**
     * Constructor
     */
    private Version() {
        // INTENTIONALLY BLANK
    }

    /**
     * Method to get the version
     *
     * @return the version
     */
    public static String getVersion() {
        return VERSION;
    }
}
