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

import static java.lang.String.format;

import io.github.dhoard.agent.isolator.util.ChildFirstURLClassLoader;
import io.github.dhoard.agent.isolator.util.Logger;
import io.github.dhoard.agent.isolator.util.Version;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Isolator agent for loading and running Java agents.
 * <p>
 * This class is responsible for loading Java agents from specified jar files and executing their main methods.
 * It uses a custom classloader to ensure that the agent classes are loaded correctly.
 */
@SuppressWarnings("PMD.EmptyCatchBlock")
public class IsolatorAgent {

    private static final Logger LOGGER = Logger.getLogger(IsolatorAgent.class);

    private static final String ISOLATOR_AGENT_THREAD_NAME = "isolator-agent";
    private static final String AGENT_MAIN_METHOD = "agentmain";

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
        LOGGER.info("IsolatorAgent %s (https://github.com/dhoard/isolator-agent)", Version.getVersion());
        LOGGER.info("agent arguments [%s]", agentArgument);

        if (agentArgument == null || agentArgument.isEmpty()) {
            throw new IllegalArgumentException("agent argument cannot be null or empty");
        }

        // TODO: Validate the agentArgument if a file that exists

        List<JavaAgent> javaAgents = Configuration.parse(Files.newBufferedReader(Paths.get(agentArgument)));

        if (!javaAgents.isEmpty()) {
            LOGGER.info("starting %d agent%s...", javaAgents.size(), javaAgents.size() == 1 ? "" : "s");

            for (int i = 0; i < javaAgents.size(); i++) {
                JavaAgent javaAgent = javaAgents.get(i);

                LOGGER.info("agent[%d] starting...", i + 1);

                Path jarPath = javaAgent.getJarPath();
                String className = javaAgent.getClassName();
                String options = javaAgent.getOptions();

                LOGGER.info("agent[%d].jarPath [%s]", i + 1, jarPath);
                LOGGER.info("agent[%d].className [%s]", i + 1, className);
                LOGGER.info("agent[%d].options [%s]", i + 1, options);

                URL jarUrl = jarPath.toUri().toURL();

                // Create a new URLClassLoader with the jar URL
                URLClassLoader urlClassLoader = new ChildFirstURLClassLoader(new URL[] {jarUrl}, null);

                try {
                    runJavaAgent(urlClassLoader, className, options, instrumentation);
                } catch (Throwable t) {
                    // Close the URLClassLoader to release resources
                    try {
                        urlClassLoader.close();
                    } catch (Throwable t2) {
                        // INTENTIONALLY BLANK
                    }

                    throw new JavaAgentException(format("agent[%d] failed to start", i + 1), t);
                }
            }

            LOGGER.info("%d agent%s started successfully", javaAgents.size(), javaAgents.size() == 1 ? "" : "s");
        } else {
            LOGGER.info("no agents to start");
        }
    }

    /**
     * Run the Java agent.
     *
     * @param urlClassLoader  the URLClassLoader to use for loading the Java agent
     * @param className       the name of the Java agent class
     * @param options         the options to pass to the Java agent
     * @param instrumentation the Instrumentation instance
     * @throws Throwable if an error occurs during Java agent execution
     */
    private static void runJavaAgent(
            URLClassLoader urlClassLoader, String className, String options, Instrumentation instrumentation)
            throws Throwable {
        final AtomicReference<Throwable> throwableAtomicReference = new AtomicReference<>();

        Thread thread = new Thread(() -> {
            try {
                // Set the context class loader to the new URLClassLoader
                // so that any spawned threads have the correct classloader
                Thread.currentThread().setContextClassLoader(urlClassLoader);

                // Load the Java agent class
                Class<?> javaAgentClass = urlClassLoader.loadClass(className);

                // Resolve the Java agent main method
                Method javaAgentMainMethod =
                        javaAgentClass.getMethod(AGENT_MAIN_METHOD, String.class, Instrumentation.class);

                // Invoke the Java agent main method
                javaAgentMainMethod.invoke(null, options, instrumentation);
            } catch (Throwable t) {
                throwableAtomicReference.set(t);
            }
        });

        thread.setName(ISOLATOR_AGENT_THREAD_NAME);
        thread.start();
        thread.join();

        if (throwableAtomicReference.get() != null) {
            throw new JavaAgentException("agent failed to start", throwableAtomicReference.get());
        }
    }
}
