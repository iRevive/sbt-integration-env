lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvTerminationStrategy := _root_.io.github.irevive.TerminationStrategy.Never,
    Test / testOptions                := integrationEnvTestOpts.value,
    integrationEnvProvider            := IntegrationEnv.DockerCompose.Provider(
      "dev-it-env",
      baseDirectory.value / "docker-compose.yml",
      None
    )
  )
