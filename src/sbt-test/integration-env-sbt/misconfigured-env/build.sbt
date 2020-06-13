lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvProvider := {
      new IntegrationEnvProvider {
        import _root_.io.github.irevive.EnvConfigurationError

        override val name: String = "failing-env"

        override def create(mode: EnvMode): Either[EnvConfigurationError, IntegrationEnv] =
          Left(EnvConfigurationError.NotAvailable)
      }
    }
  )
