# sbt-integration-env

[![Build Status](https://github.com/iRevive/sbt-integration-env/workflows/CI/badge.svg)](https://github.com/iRevive/fmq/sbt-integration-env)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env)

sbt-integration-env is an SBT plugin for 

## Quick Start

To use sbt-integration-env in an existing SBT project (1.3.+), add the following dependency to your `plugins.sbt`:
 
```scala
addSbtPlugin("io.github.irevive" % "sbt-integration-env" % "0.1.1")
```

## Usage Guide

The plugin should be enabled manually for a specific project:

```
lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvProvider := new DockerComposeEnvProvider("ci-it-env", baseDirectory.value / "docker-compose.yml", None),
    Test / testOptions := integrationEnvTestOpts.value
  )
```

## Automated environment management upon tests execution

```
integrationEnvProvider := new DockerComposeEnvProvider("my-it-env", baseDirectory.value / "docker-compose.yml", None),
Test / testOptions     := integrationEnvTestOpts.value
```

Which such a config SBT will start environment during setup phase and terminate environment during clean up phase.

Based on the env mode uses different strategy:
1) CI - recreates environment ;
2) Dev - creates environment but does not terminate it.

## Commands

`integrationEnvStart` - Start local integration environment
`integrationEnvStop` - Stop local integration environment
