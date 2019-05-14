ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0"
ThisBuild / organization := "io.github.martinpallmann"
ThisBuild / organizationName := "Martin Pallmann"

cancelable in Global := true

lazy val common = Seq(
  scalacOptions ++= ScalacOptions(),
  testFrameworks += new TestFramework("minitest.runner.Framework")
)

lazy val `config-db` = project
  .settings(common)

lazy val `db-migrations` = project
  .dependsOn(`config-db`)
  .settings(
    common, 
    libraryDependencies ++= Dependencies.`db-migrations`,
    mainClass in (Compile, run) := Some("fondue.db.migrations.Main")
  )

lazy val server = project
  .dependsOn(`config-db`)
  .settings(
    common,
    libraryDependencies ++= Dependencies.server,
    mainClass in (Compile, run) := Some("fondue.server.Main")
  )

enablePlugins(JavaAppPackaging)
