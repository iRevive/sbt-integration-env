# sbt-integration-env

[![Build Status](https://github.com/iRevive/sbt-integration-env/workflows/CI/badge.svg)](https://github.com/iRevive/sbt-integration-env/actions?query=branch%3Amaster+workflow%3ACI+)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env)

sbt-integration-env is an SBT plugin for the environment management.  
You can create and terminate environment within SBT, or configure automated creation and cleanup during execution of tests.  

## Quick Start

To use sbt-integration-env in an existing SBT project (1.3.+), add the following dependency to your `plugins.sbt`:
 
```sbt
addSbtPlugin("io.github.irevive" % "sbt-integration-env" % "0.1.0")
```

## Usage Guide

There are three steps to start using the plugin:

```sbt
lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin) // 1
  .settings(
    integrationEnvProvider := new DockerComposeEnvProvider(s"${name.value}-it-env", baseDirectory.value / "docker-compose.yml", None), // 2
    Test / testOptions     := integrationEnvTestOpts.value // 3
  )
```

1) Enable plugin for a specific project.
2) Configure environment provider. You can use `DockerComposeEnvProvider` or implement a custom one.
3) Optional. Enable automated management during the testing phase. [Details](#automated-creation-and-termination-during-execution-of-tests)

## Environment provider

There is a predefined `DockerComposeEnvProvider` which uses `docker-compose` for environment manipulations.  

```scala
new DockerComposeEnvProvider(
  composeProjectName = s"${name.value}-it-env", // 1
  dockerComposeFile = baseDirectory.value / "docker-compose.yml", // 2
  network = Some(s"${name.value}-it-network") // 3
)
```

1) Name of the docker-compose project. [Details](https://docs.docker.com/compose/reference/overview/#use--p-to-specify-a-project-name)
2) Path to the docker-compose file. 
3) External docker network. [Details](#external-docker-network)

## Manual creation and termination of environment

`integrationEnvStart` - create environment.  
`integrationEnvStop` - terminate environment.

## Automated creation and termination during execution of tests

[Configuration example](https://github.com/iRevive/sbt-integration-env/tree/master/examples/simple) 

Setting:
```sbt
Test / testOptions := integrationEnvTestOpts.value
```

The plugin uses [Setup and Cleanup](https://scala-sbt.org/1.x/docs/Testing.html#Setup+and+Cleanup) hooks for manipulations with environment.  
Add test options to the project settings:
Based on the state (CI, Dev) the plugin behaves differently, the state is determine by SBT `insideCI` command.

Behaviors:

1) CI 
Before tests: terminate if exists, then create new.  
After tests: terminate.  

2) Dev
 
Before tests: create new if not exists.  
After tests: do not terminate.
  
## External docker network

[Configuration example](https://github.com/iRevive/sbt-integration-env/tree/master/examples/external-docker-network) 

External docker network is useful when you are running SBT build in docker container within [dind](https://hub.docker.com/_/docker) runner.  
At this point you should use a shared network across all components: SBT container and docker-compose.  
