package io.github.irevive

import sbt.Keys._
import sbt._

object IntegrationEnvPlugin extends AutoPlugin {

  override def trigger = noTrigger

  object autoImport {
    type EnvMode                  = _root_.io.github.irevive.EnvMode
    type EnvConfigurationError    = _root_.io.github.irevive.EnvConfigurationError
    type IntegrationEnv           = _root_.io.github.irevive.IntegrationEnv
    type IntegrationEnvProvider   = _root_.io.github.irevive.IntegrationEnvProvider
    type DockerComposeEnvProvider = _root_.io.github.irevive.DockerComposeEnvProvider

    lazy val integrationEnvStart    = taskKey[Unit]("Start local integration environment")
    lazy val integrationEnvStop     = taskKey[Unit]("Stop local integration environment")
    lazy val integrationEnv         = taskKey[IntegrationEnv]("Configured integration environment")
    lazy val integrationEnvProvider = taskKey[IntegrationEnvProvider]("Integration environment provider")
    lazy val integrationEnvTestOpts = taskKey[Seq[TestOption]]("Setup and cleanup options")

    lazy val integrationEnvMode =
      settingKey[EnvMode]("Environment mode. CI - cleanup env after tests execution. Dev - keep env alive.")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] =
    Seq(
      integrationEnvMode := (if (insideCI.value) EnvMode.CI else EnvMode.Dev),
      integrationEnv := {
        val provider = integrationEnvProvider.value
        val mode     = integrationEnvMode.value

        provider
          .create(mode)
          .fold(
            e => throw new IllegalStateException(s"Integration environment cannot be created by [${provider.name}]. Error: $e"),
            identity
          )
      },
      integrationEnvStart := {
        val env = integrationEnv.value
        val log = streams.value.log

        log.info("Creating integration environment")
        env.create()
      },
      integrationEnvStop := {
        val env = integrationEnv.value
        val log = streams.value.log

        log.info("Terminating integration environment")
        env.terminate()
      },
      integrationEnvTestOpts := Def.task {
        val env  = integrationEnv.value
        val mode = integrationEnvMode.value
        val log  = streams.value.log

        val setup = Tests.Setup { () =>
          def ci(): Unit = {
            env.terminate()
            env.create()
          }

          def dev(): Unit =
            env.create()

          log.info(s"Preparing integration environment. $mode mode")
          mode.fold(ci(), dev())
        }

        val cleanup = Tests.Cleanup { () =>
          log.info(s"Cleaning up integration environment. $mode mode")
          mode.fold(env.terminate(), ())
        }

        Seq(setup, cleanup)
      }.value
    )

}
