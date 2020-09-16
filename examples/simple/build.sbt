lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvProvider := new DockerComposeEnvProvider("it-env", baseDirectory.value / "docker-compose.yml", None),
    Test / testOptions     := integrationEnvTestOpts.value,
    Test / fork            := true
  )