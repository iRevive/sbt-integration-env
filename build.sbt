lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name                := "sbt-integration-env",
    organization        := "io.github.irevive",
    crossSbtVersions    := Seq("1.3.12"),
    scriptedBufferLog   := false,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    homepage            := Some(url("https://github.com/iRevive/sbt-integration-env")),
    licenses            := List("MIT" -> url("http://opensource.org/licenses/MIT")),
    developers          := List(Developer("iRevive", "Maksim Ochenashko", "", url("https://github.com/iRevive")))
  )
