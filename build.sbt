lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
    .settings(
      name := "sbt-integration-env",
      crossSbtVersions := Seq("1.3.12"),

      scriptedBufferLog := false,
      Test / test := {
        val _ = (Test / test).value
        scripted.toTask("").value
      },
      scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
        "-Xmx1024M", "-Dplugin.version=" + version.value
      )
    )

inThisBuild(
  Seq(
    organization := "io.github.irevive",
    homepage     := Some(url("https://github.com/iRevive/sbt-integration-env")),
    licenses     := List("MIT" -> url("http://opensource.org/licenses/MIT")),
    developers   := List(Developer("iRevive", "Maksim Ochenashko", "", url("https://github.com/iRevive")))
  )
)