package io.github.irevive

import sbt.Keys._
import sbt._

object IntegrationEnvPlugin extends AutoPlugin {

  override def trigger = noTrigger

  object autoImport {
    type TerminationStrategy   = _root_.io.github.irevive.TerminationStrategy
    type EnvConfigurationError = _root_.io.github.irevive.EnvConfigurationError
    type IntegrationEnv        = _root_.io.github.irevive.IntegrationEnv

    val IntegrationEnv = _root_.io.github.irevive.IntegrationEnv

    lazy val integrationEnvStart    = taskKey[Unit]("Start local integration environment")
    lazy val integrationEnvStop     = taskKey[Unit]("Stop local integration environment")
    lazy val integrationEnvTestOpts = taskKey[Seq[TestOption]]("Setup and cleanup options")
    lazy val integrationEnv         = taskKey[IntegrationEnv]("Configured integration environment")
    lazy val integrationEnvProvider = taskKey[IntegrationEnv.Provider]("Integration environment provider")

    lazy val integrationEnvTerminationStrategy =
      settingKey[TerminationStrategy](
        "Termination strategy. UponTestCompletion - terminate after execution of tests. Never - never terminate."
      )
  }

  import IntegrationEnvFormats._
  import autoImport._

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      integrationEnvTerminationStrategy := {
        if (insideCI.value) TerminationStrategy.UponTestCompletion
        else TerminationStrategy.Never
      },
      integrationEnv := {
        val projectName = name.value
        val s           = streams.value
        val cacheStore  = s.cacheStoreFactory.make("it-env")
        val log         = s.log

        val prev     = integrationEnv.previous
        val strategy = integrationEnvTerminationStrategy.value
        val provider = integrationEnvProvider.value

        def create(provider: IntegrationEnv.Provider): IntegrationEnv =
          provider
            .create(strategy)
            .fold(
              e =>
                throw new IllegalStateException(
                  s"Project [$projectName]. Integration environment cannot be created by [${provider.name}]. Error: $e"
                ),
              identity
            )

        val cachedProvider =
          Tracked.inputChanged[IntegrationEnv.Provider, IntegrationEnv](cacheStore) { (changed, in) =>
            if (changed) {
              log.info("Integration provider has changed. Applying a new configuration")
              create(in)
            } else {
              prev.getOrElse(create(in))
            }
          }

        cachedProvider(provider)
      },
      integrationEnvStart := {
        val projectName = name.value
        val env         = integrationEnv.value
        val log         = streams.value.log

        log.info(s"Project [$projectName]. Creating integration environment")
        env.create()
      },
      integrationEnvStop := {
        val projectName = name.value
        val env         = integrationEnv.value
        val log         = streams.value.log

        log.info(s"Project [$projectName]. Terminating integration environment")
        env.terminate()
      },
      integrationEnvTestOpts := Def.task {
        val projectName = name.value
        val env         = integrationEnv.value
        val strategy    = integrationEnvTerminationStrategy.value
        val log         = streams.value.log

        val setup = Tests.Setup { () =>
          log.info(s"Project [$projectName]. Preparing integration environment. Strategy $strategy")
          env.prepare(strategy)
        }

        val cleanup = Tests.Cleanup { () =>
          def terminate(): Unit = {
            log.info(s"Project [$projectName]. Cleaning up integration environment")
            env.terminate()
          }

          strategy.fold(terminate(), ())
        }

        Seq(setup, cleanup)
      }.value
    )

}
