lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvTerminationStrategy := _root_.io.github.irevive.TerminationStrategy.UponTestCompletion,
    Test / testOptions                := integrationEnvTestOpts.value,
    integrationEnvProvider            := IntegrationEnv.DockerCompose.Provider(
      "ci-it-env",
      baseDirectory.value / "docker-compose.yml",
      None
    )
  )
