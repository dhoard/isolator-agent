# IsolatorAgent

IsolatorAgent is a Java agent that allows you to run multiple instances of the same agent with different configurations in the same JVM.

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

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

---

Copyright (C) 2025 Doug Hoard