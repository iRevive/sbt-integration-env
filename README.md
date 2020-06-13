# sbt-integration-env

[![Build Status](https://github.com/iRevive/sbt-integration-env/workflows/CI/badge.svg)](https://github.com/iRevive/fmq/sbt-integration-env)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.irevive/sbt-integration-env)

sbt-integration-env is an SBT plugin for 

## Quick Start

To use sbt-integration-env in an existing SBT project, add the following dependency to your `plugins.sbt`:
 
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


## Supported protocols

* TCP
* InProc

## Sockets matrix

| Socket | Can publish | Can receive |
|--------|-------------|-------------|
| Pub    | true        | false       |
| Sub    | false       | true        |
| XPub   | true        | true        |
| XSub   | true        | true        |
| Pull   | false       | true        |
| Push   | true        | false       |
| Rep    | true        | true        |
| Req    | true        | true        |
| Router | true        | true        |
| Dealer | true        | true        |

## Benchmarks

ƒMQ provides a great message throughput comparing to the native implementation.

[ƒMQ](https://github.com/iRevive/fmq/blob/master/bench/src/main/scala/io/fmq/SocketBenchmark.scala) msgs/s:

| Message size (bytes) | Throughput |
|----------------------|------------|
| 128                  | 1960737    |
| 256                  | 1511724    |
| 512                  | 862353     |
| 1024                 | 498450     |

[ØMQ](http://wiki.zeromq.org/results:ib-tests-v206) msgs/s: 

| Message size (bytes) | Throughput |
|----------------------|------------|
| 128                  | 3885802    |
| 256                  | 2689235    |
| 512                  | 1598083    |
| 1024                 | 867274     |

Hardware:  
MacBook Pro (15-inch, 2016)  
2,6 GHz Quad-Core Intel Core i7  
16 GB 2133 MHz LPDDR3  