lazy val root = project
  .in(file("."))
  .enablePlugins(IntegrationEnvPlugin)
  .settings(
    integrationEnvProvider := IntegrationEnv.DockerCompose.Provider(
      composeProjectName = "it-env",
      dockerComposeFile = baseDirectory.value / "docker-compose.yml",
      network = None
    ),
    Test / testOptions := integrationEnvTestOpts.value,
    Test / fork        := true
  )
