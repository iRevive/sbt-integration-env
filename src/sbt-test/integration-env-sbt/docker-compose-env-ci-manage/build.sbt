lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvMode     := _root_.io.github.irevive.EnvMode.CI,
    integrationEnvProvider := new DockerComposeEnvProvider("ci-it-env", baseDirectory.value / "docker-compose.yml", None),
    Test / testOptions     := integrationEnvTestOpts.value
  )
