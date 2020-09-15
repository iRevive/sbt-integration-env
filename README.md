# sbt-integration-env

[![Build Status](https://github.com/iRevive/sbt-integration-env/workflows/CI/badge.svg)](https://github.com/iRevive/sbt-integration-env/actions?query=branch%3Amaster+workflow%3ACI+)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env)

sbt-integration-env is an SBT plugin for environment management.  
You can create and terminate the environment within SBT, or configure automated management during the execution of tests.

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
    integrationEnvProvider := new DockerComposeEnvProvider( // 2
      composeProjectName = s"${name.value}-it-env", 
      dockerComposeFile = baseDirectory.value / "docker-compose.yml", 
      network = None
    ),
    Test / testOptions := integrationEnvTestOpts.value, // 3
    Test / fork := true
  )
```

1) Enable plugin for a specific project.
2) Configure environment provider. You can use `DockerComposeEnvProvider` or implement a custom one.
3) Optional. Enable automated management during the testing phase. [Details](#automated-creation-and-termination)

## Environment provider

There is a predefined `DockerComposeEnvProvider` which uses `docker-compose` for manipulations with the environment.  

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

## Manual creation and termination

`integrationEnvStart` - create environment.  
`integrationEnvStop` - terminate environment.

## Automated creation and termination

[Configuration example](https://github.com/iRevive/sbt-integration-env/tree/master/examples/simple) 

Setting:
```sbt
Test / testOptions := integrationEnvTestOpts.value
```

The plugin uses [Setup and Cleanup](https://scala-sbt.org/1.x/docs/Testing.html#Setup+and+Cleanup) hooks for manipulations. 
Based on the termination strategy (UponTestCompletion, OnSbtExit, Never) the plugin behaves differently.  
The strategy is determined by SBT's `insideCI` command: if `insideCI` is true the `UponTestCompletion` strategy is selected, otherwise the `OnSbtExit` is used.   
It can be configured explicitly as well:  
```sbt
integrationEnvTerminationStrategy := TerminationStrategy.UponTestCompletion
```

Termination strategies:

1) UponTestCompletion  
Before tests: terminate if exist, then create new    
After tests: terminate  
On Sbt exit: terminate  

2) OnSbtExit  
Before tests: create new if not exist   
After tests: do not terminate  
On Sbt exit: terminate
  
3) Never
Before tests: create new if not exist    
After tests: do not terminate  
On Sbt exit: do not terminate  

## External docker network

[Configuration example](https://github.com/iRevive/sbt-integration-env/tree/master/examples/external-docker-network) 

External docker network is useful when you are running SBT build in docker container within [dind](https://hub.docker.com/_/docker).  
At this point, you should use a shared network across all components: SBT container and docker-compose.  
