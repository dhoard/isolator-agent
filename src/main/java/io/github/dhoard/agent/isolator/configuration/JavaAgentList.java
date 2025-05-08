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

import java.util.List;

/**
 * Represents a list of Java agents.
 * <p>
 * This class contains a list of JavaAgent objects, which represent the individual Java agents to be loaded and run.
 */
public class JavaAgentList {

    private List<JavaAgent> javaAgents;

    /**
     * Default constructor for JavaAgentList.
     * <p>
     * This constructor is intentionally empty and is provided for serialization purposes.
     */
    public JavaAgentList() {
        // INTENTIONALLY BLANK
    }

    /**
     * Sets the list of Java agents.
     *
     * @param javaAgents List of JavaAgents
     */
    public void setJavaAgents(List<JavaAgent> javaAgents) {
        this.javaAgents = javaAgents;
    }

    /**
     * Gets the list of Java agents.
     *
     * @return List of JavaAgents
     */
    public List<JavaAgent> getJavaAgents() {
        return javaAgents;
    }
}
