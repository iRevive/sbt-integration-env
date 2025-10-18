lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvProvider := IntegrationEnv.DockerCompose.Provider(
      composeProjectName = s"${name.value}-it-env",
      dockerComposeFile = baseDirectory.value / "docker-compose.yml",
      network = Some(s"${name.value}-it-network")
    ),
    Test / testOptions := integrationEnvTestOpts.value,
    Test / fork        := true
  )
