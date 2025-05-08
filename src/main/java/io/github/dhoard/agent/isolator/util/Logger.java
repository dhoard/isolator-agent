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

import static java.lang.String.format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Logger class for logging messages with timestamps and thread information.
 * <p>
 * This class provides a simple logging mechanism that formats log messages with the current date and time,
 * the name of the thread, and the class name from which the log message is generated.
 */
public class Logger {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    private final String className;

    /**
     * Constructor for Logger.
     *
     * @param clazz Class for which the logger is created
     */
    private Logger(Class<?> clazz) {
        this.className = clazz.getName();
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message Message to log
     */
    public void info(String message) {
        info("%s", message);
    }

    /**
     * Logs a message at the INFO level with formatted arguments.
     *
     * @param format  Format string
     * @param objects Arguments to format the message
     */
    public void info(String format, Object... objects) {
        System.out.printf(
                "%s | %s | INFO | %s | %s%n",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                Thread.currentThread().getName(),
                className,
                format(format, objects));
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param message Message to log
     */
    public void error(String message) {
        error("%s", message);
    }

    /**
     * Logs a message at the ERROR level with formatted arguments.
     *
     * @param format  Format string
     * @param objects Arguments to format the message
     */
    public void error(String format, Object... objects) {
        System.err.printf(
                "%s | %s | ERROR | %s | %s%n",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                Thread.currentThread().getName(),
                className,
                format(format, objects));
    }

    /**
     * Creates a new Logger instance for the specified class.
     *
     * @param clazz Class for which the logger is created
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }
}
