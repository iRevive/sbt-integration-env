lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvTerminationStrategy := _root_.io.github.irevive.TerminationStrategy.OnSbtExit,
    Test / testOptions                := integrationEnvTestOpts.value,
    integrationEnvProvider := new DockerComposeEnvProvider(
      "ci-it-env",
      baseDirectory.value / "docker-compose.yml",
      None
    )
  )
