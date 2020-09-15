lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvTerminationStrategy := _root_.io.github.irevive.TerminationStrategy.Never,
    Test / testOptions                := integrationEnvTestOpts.value,
    integrationEnvProvider := new DockerComposeEnvProvider(
      "dev-it-env",
      baseDirectory.value / "docker-compose.yml",
      None
    )
  )
