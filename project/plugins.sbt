addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.7.0")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"       % "2.5.2")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.24.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
