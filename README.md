[![Build](https://github.com/dhoard/isolator-agent/actions/workflows/build.yaml/badge.svg)](https://github.com/dhoard/isolator-agent/actions/workflows/build.yaml) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/f2f60b7517874ce6a8cc75a6b2e18a46)](https://app.codacy.com/gh/dhoard/isolator-agent/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) <a href="#"><img src="https://img.shields.io/badge/JDK%20compatibility-8+-blue.svg" alt="java 8+"></a> <a href="#"><img src="https://img.shields.io/badge/license-Apache%202.0-blue.svg" alt="Apache 2.0"></a>

# IsolatorAgent

IsolatorAgent is a Java agent that allows you to run multiple instances of the same agent with different configurations in the same JVM - completely isolated

## Why?

Sometimes you want to run multiple instances of the same Java agent with different configurations.

Okay... but why?

For example, you may want to run two instances of the Prometheus JMX Exporter with different configurations.

- Configuration 1 could be used to export summary metrics at a short scrape interval (quick scrape)


- Configuration 2 could be used to export detailed metrics at a longer scrape interval (slow scrape)

The IsolatorAgent allows you to do this by loading the same agent multiple times with different configurations in isolation.

See the [real-world example](#real-world-example) below for a practical use case.

## Usage

Define a YAML configuration file with the following structure:

```yaml
javaAgents:
  - jarPath: some-agent.jar
    className: agent.ClassName
    options: options 1
  - jarPath: some-agent.jar
    className: agent.ClassName
    options: options 2
  - jarPath: some-agent.jar
    className: agent.ClassName
    options: options 3
    enabled: false
```

*Notes*

- The `jarPath` is the path to the agent JAR file.
- The `className` is the fully qualified name of the agent class.
- The `options` are the options to pass to the agent.

**Notes**

- The `enabled` field is optional. If set to `false`, the agent configuration will be ignored.

## Examples

### Example 1

```bash
./examples/hello-world.sh
```

Prometheus JMX Exporter metrics are available at `http://localhost:8080/metrics`.

### Example 2

```bash
./examples/hello-world-2.sh
```

Prometheus JMX Exporter metrics are available at `http://localhost:8080/metrics` and `http://localhost:9090/metrics`.

- The metric and label names have difference cases.

## Real-World Example

Running two instances of the Prometheus JMX Exporter with Kafka.

### Configuration

Install the IsolatorAgent and the Prometheus JMX Exporter JARs:

```bash
/opt/prometheus/isolator-agent-0.0.1.jar
/opt/prometheus/jmx_prometheus_javaagent-1.2.0.jar
```

Create the IsolatorAgent YAML configuration file `/opt/prometheus/isolator-agent.yaml`:

```yaml
javaAgents:
  - jarPath: /opt/prometheus/jmx_prometheus_javaagent-1.2.0-post.jar
    className: io.prometheus.jmx.JavaAgent
    options: 8080:/opt/prometheus/all-lowercase.yaml
  - jarPath: /opt/prometheus/jmx_prometheus_javaagent-1.2.0-post.jar
    className: io.prometheus.jmx.JavaAgent
    options: 9090:/opt/prometheus/all-camelcase.yaml
```

Create the Prometheus JMX Exporter YAML configuration files:

`/opt/prometheus/all-lowercase.yaml`

```yaml
---
startDelaySeconds: 120
lowercaseOutputName: true
lowercaseOutputLabelNames: true
rules:
- pattern: ".*"
```

`/opt/prometheus/all-camelcase.yaml`

```yaml
---
startDelaySeconds: 120
httpServer:
  authentication:
    basic:
      username: Prometheus
      password: secret

rules:
- pattern: ".*"
```

Added the IsolatorAgent to a Kafka broker Java command line:

Example:

```bash
KAFKA_HEAP_OPTS=-javaagent:/opt/prometheus/isolator-agent-0.0.1.jar=/opt/prometheus/isolator-agent.yaml -Xms1g -Xmx6g -XX:MetaspaceSize=96m -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=16M -XX:MinMetaspaceFreeRatio=50 -XX:MaxMetaspaceFreeRatio=80
```

### Viewing Metrics

The IsolatorAgent will load the Prometheus JMX Exporter agent twice, each with its own configuration. These two instances are completely isolated from each other.

Access the Prometheus JMX Exporter at `http://<SERVER>>:8080/metrics` and `http://<SERVER>:9090/metrics`.

- The metrics exported on port `8080` will have lowercase metric and label names.


- The metrics exported on port `9090` will require HTTP authentication and use camelcase metric and label names.

**NOTES**

- The Prometheus JMX Exporter currently does not dynamically reload `httpServer` configuration.

## Building

To build the IsolatorAgent, you need to have Java 8 or higher installed.

```bash
./mvnw clean package
```

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

---

Copyright (C) 2025-Present Doug Hoard