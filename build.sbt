ThisBuild / githubWorkflowTargetBranches        := Seq("master")
ThisBuild / githubWorkflowTargetTags           ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name                := "sbt-integration-env",
    organization        := "io.github.irevive",
    crossSbtVersions    := Seq("1.10.2"),
    scriptedBufferLog   := false,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    homepage            := Some(url("https://github.com/iRevive/sbt-integration-env")),
    licenses            := List(License.MIT),
    developers          := List(Developer("iRevive", "Maksym Ochenashko", "", url("https://github.com/iRevive")))
  )
