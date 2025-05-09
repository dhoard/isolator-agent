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

/**
 * Exception thrown when there is a Java agent failure
 */
public class JavaAgentException extends RuntimeException {

    /**
     * Constructs a new JavaAgentException with the specified detail message.
     *
     * @param message The message
     */
    public JavaAgentException(String message) {
        super(message);
    }

    /**
     * Constructs a new JavaAgentException with the specified detail message and cause.
     *
     * @param message The message
     * @param cause The cause
     */
    public JavaAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JavaAgentException with the specified cause.
     *
     * @param cause The cause
     */
    public JavaAgentException(Throwable cause) {
        super(cause);
    }
}
