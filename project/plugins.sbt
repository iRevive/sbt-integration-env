addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.6.1")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"   % "2.5.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
