#!/bin/bash

#
# Copyright (C) 2025-present Doug Hoard
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Save the original directory
ORIGINAL_DIR="$(pwd)"

# Get the directory of this script
EXAMPLES_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define a cleanup function to return to the original directory
cleanup() {
    cd "$ORIGINAL_DIR"
}
trap cleanup EXIT INT TERM

# Change to script directory
cd "$EXAMPLES_DIR" || exit 1

# Remove the old jar and Java class if it exists
rm -Rf isolator-agent-*.jar HelloWorld.class || exit 1

# Compile the Java test class
javac HelloWorld.java || exit 1

# Get the project version from Maven
VERSION=$(cd .. && ./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout) || exit 1

# Check for the agent jar, copy from ../target if missing
AGENT_JAR="isolator-agent-$VERSION.jar"

# Remove the old agent jar if it exists
if [[ -f "$AGENT_JAR" ]]; then
    rm "$AGENT_JAR" || exit 1
fi

# Copy the agent jar from ../target if it exists
cp "../target/$AGENT_JAR" . || exit 1

# Run the Java class with the Java agent
java -javaagent:"$AGENT_JAR"=hello-world-2.yaml -cp . HelloWorld
