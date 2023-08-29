addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"   % "2.5.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
