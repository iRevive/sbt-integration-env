# sbt-integration-env

[![Build Status](https://github.com/iRevive/sbt-integration-env/workflows/CI/badge.svg)](https://github.com/iRevive/sbt-integration-env/actions)
[![sbt-integration-env Scala version support](https://index.scala-lang.org/irevive/sbt-integration-env/sbt-integration-env/latest-by-scala-version.svg?targetType=Sbt)](https://index.scala-lang.org/irevive/sbt-integration-env/sbt-integration-env)

sbt-integration-env is an SBT plugin for environment management.  
You can create and terminate environments within sbt, or configure automated management during test execution.

## Quick Start

To use sbt-integration-env in an existing SBT project (1.3.+), add the following dependency to your `plugins.sbt`:
 
```sbt
addSbtPlugin("io.github.irevive" % "sbt-integration-env" % "0.4.0")
```

## Usage Guide

There are three steps to start using the plugin:

```sbt
lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin) // 1
  .settings(
    integrationEnvProvider := IntegrationEnv.DockerCompose.Provider( // 2
      composeProjectName = s"${name.value}-it-env", 
      dockerComposeFile = baseDirectory.value / "docker-compose.yml", 
      network = None
    ),
    Test / testOptions := integrationEnvTestOpts.value, // 3
    Test / fork := true
  )
```

1) Enable the plugin for a specific project.  
2) Configure the environment provider. You can use `DockerComposeEnvProvider` or implement a custom one.  
3) Optional. Enable automated management during the testing phase. [Details](#automated-creation-and-termination)

## Environment provider

A predefined `DockerComposeEnvProvider` uses `docker compose` to manage the environment.

```scala
IntegrationEnv.DockerCompose.Provider(
  composeProjectName = s"${name.value}-it-env", // 1
  dockerComposeFile = baseDirectory.value / "docker-compose.yml", // 2
  network = Some(s"${name.value}-it-network") // 3
)
```

1) Name of the docker-compose project. [Details](https://docs.docker.com/compose/reference/overview/#use--p-to-specify-a-project-name)
2) Path to the docker-compose file. 
3) External docker network. [Details](#external-docker-network)

## Manual creation and termination

`integrationEnvStart` - create the environment.  
`integrationEnvStop` - terminate the environment.

## Automated creation and termination

[Configuration example](https://github.com/iRevive/sbt-integration-env/tree/master/examples/simple) 

The following setting controls the automated lifecycle management:
```sbt
Test / testOptions := integrationEnvTestOpts.value
```

The plugin uses [Setup and Cleanup](https://scala-sbt.org/1.x/docs/Testing.html#Setup+and+Cleanup) hooks to manage the environment.  
Depending on the termination strategy, such as `UponTestCompletion` or `Never`, the plugin behaves differently.  

The strategy is determined by sbt `insideCI` command: if `insideCI` is true the `UponTestCompletion` strategy is selected, otherwise `Never` is used.  
It can also be configured explicitly: 
```sbt
integrationEnvTerminationStrategy := TerminationStrategy.UponTestCompletion
```

Termination strategies:

| Strategy             | Before tests                                              | After tests               |
|----------------------|-----------------------------------------------------------|---------------------------|
| `UponTestCompletion` | Terminate the existing environment, then create a new one | Terminate the environment |  
| `Never`              | Create a new one if it doesn't exist yet                  | Do nothing                |


## External docker network

[Configuration example](https://github.com/iRevive/sbt-integration-env/tree/master/examples/external-docker-network) 

An external docker network is useful when running an SBT build in a docker container within [dind](https://hub.docker.com/_/docker).  
In this case, you should use a shared network across all components: the SBT container and docker-compose.  
