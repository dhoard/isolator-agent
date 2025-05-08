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

import io.github.dhoard.agent.isolator.configuration.JavaAgent;
import io.github.dhoard.agent.isolator.configuration.YamlParser;
import io.github.dhoard.agent.isolator.util.ChildFirstURLClassLoader;
import io.github.dhoard.agent.isolator.util.Logger;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Isolator agent for loading and running Java agents.
 * <p>
 * This class is responsible for loading Java agents from specified jar files and executing their main methods.
 * It uses a custom classloader to ensure that the agent classes are loaded correctly.
 */
public class IsolatorAgent {

    private static final Logger LOGGER = Logger.getLogger(IsolatorAgent.class);

    private static final String AGENTMAIN = "agentmain";

    /**
     * Default constructor for IsolatorAgent.
     * <p>
     * This constructor is intentionally left blank.
     */
    public IsolatorAgent() {
        // INTENTIONALLY BLANK
    }

    /**
     * Java agent main
     *
     * @param agentArgument agentArgument
     * @param instrumentation instrumentation
     * @throws Exception if an error occurs during agent execution
     */
    public static void agentmain(String agentArgument, Instrumentation instrumentation) throws Exception {
        premain(agentArgument, instrumentation);
    }

    /**
     * Java agent premain
     *
     * @param agentArgument agentArgument
     * @param instrumentation instrumentation
     * @throws Exception if an error occurs during agent execution
     */
    public static void premain(String agentArgument, Instrumentation instrumentation) throws Exception {
        LOGGER.info("Starting agents...");
        LOGGER.info("agent arguments [%s]", agentArgument);

        if (agentArgument == null || agentArgument.isEmpty()) {
            throw new IllegalArgumentException("agent argument cannot be null or empty");
        }

        // TODO: Validate the agentArgument if a file that exists

        List<JavaAgent> javaAgents = YamlParser.parse(Files.newBufferedReader(Paths.get(agentArgument)));

        if (!javaAgents.isEmpty()) {
            ExecutorService executor = Executors.newFixedThreadPool(javaAgents.size());
            List<URLClassLoader> urlClassLoaders = new ArrayList<>(javaAgents.size());
            CompletionService<Void> completion = new ExecutorCompletionService<>(executor);

            for (int i = 0; i < javaAgents.size(); i++) {
                JavaAgent javaAgent = javaAgents.get(i);

                LOGGER.info("Starting agent[%d]...", i + 1);

                String jarPath = javaAgent.getJarPath();
                String className = javaAgent.getClassName();
                String options = javaAgent.getOptions();

                LOGGER.info("agent[%d] jar [%s]", i + 1, jarPath);
                LOGGER.info("agent[%d] className [%s]", i + 1, className);
                LOGGER.info("agent[%d] options [%s]", i + 1, options);

                URL jarUrl = Paths.get(jarPath).toUri().toURL();
                URLClassLoader urlClassLoader = new ChildFirstURLClassLoader(new URL[] {jarUrl}, null);
                urlClassLoaders.add(urlClassLoader);

                completion.submit(() -> {
                    Thread.currentThread().setContextClassLoader(urlClassLoader);
                    Class<?> agentClass = urlClassLoader.loadClass(className);
                    Method agentMainMethod = agentClass.getMethod(AGENTMAIN, String.class, Instrumentation.class);
                    agentMainMethod.invoke(null, options, instrumentation);
                    return null;
                });
            }

            // Wait for all agents to start successfully or fail
            for (int i = 0; i < javaAgents.size(); i++) {
                try {
                    completion.take().get();
                } catch (Exception e) {
                    LOGGER.error("agent[%d] failed during startup", i + 1, e.getCause());

                    executor.shutdownNow();

                    for (URLClassLoader urlClassLoader : urlClassLoaders) {
                        try {
                            urlClassLoader.close();
                        } catch (Throwable t) {
                            // INTENTIONALLY BLANK
                        }
                    }

                    throw new RuntimeException("Agent startup failed", e.getCause());
                }
            }
        }

        LOGGER.info("All agents started successfully");
    }
}
