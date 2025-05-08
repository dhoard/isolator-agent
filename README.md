# IsolatorAgent

IsolatorAgent is a Java agent that allows you to run multiple instances of the same agent with different configurations in the same JVM.

## Why?

Sometimes you want to run multiple instances of the same Java agent with different configurations. For example, you may want to run two instances of the Prometheus JMX Exporter with different configurations. The IsolatorAgent allows you to do this by loading the same agent multiple times with different configurations in isolation.

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
```

*Notes*

- The `jarPath` is the path to the agent JAR file.
- The `className` is the fully qualified name of the agent class.
- The `options` are the options to pass to the agent.

## Example

Run your Java application with the following command:

```bash
java -javaagent:isolator-agent.jar=path/to/isolator-agent.yaml -jar your-application.jar
```

## Real-World Example

Running two instances of the Prometheus JMX Exporter with Kafka.


### Configuration

Install the IsolatorAgent and the JMX Exporter JARs:

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

The IsolatorAgent will load the JMX Exporter agent twice, each with its own configuration. These two instances are completely isolated from each other.

Access the JMX Exporter at `http://localhost:8080/metrics` and `http://localhost:9090/metrics`.

- The metrics exported on port `8080` will have lowercase metric and label names.


- The metrics exported on port `9090` will require HTTP authentication and also will have camelcase metric and label names.

**NOTES**

- The Prometheus JMX Exporter currently does not dynamically reload `httpServer` configuration.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

---

Copyright (C) 2025-Present Doug Hoard