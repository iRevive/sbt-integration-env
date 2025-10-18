lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvProvider := IntegrationEnv.DockerCompose.Provider(
      s"${name.value}-it-env",
      baseDirectory.value / "docker-compose.yml",
      Some(s"${name.value}-it-network")
    ),
    Test / testOptions := integrationEnvTestOpts.value,
    Test / fork        := true
  )
