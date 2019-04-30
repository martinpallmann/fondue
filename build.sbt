ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0"
ThisBuild / organization     := "io.github.martinpallmann"
ThisBuild / organizationName := "Martin Pallmann"

cancelable in Global := true


lazy val common = Seq(
  scalacOptions ++= ScalacOptions(),
  testFrameworks += new TestFramework("minitest.runner.Framework")
)

lazy val fondue = (project in file("."))
  .settings(
    common,
    libraryDependencies := Dependencies.server,
    mainClass in (Compile, run) := Some("fondue.server.Main")
  )

enablePlugins(JavaAppPackaging)
