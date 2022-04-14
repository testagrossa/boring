name := "boring"

scalaVersion := "2.13.6"

name := "boring"
scalaVersion := "2.13.3"
version := "0.0.1"
organization := "Prom3th3us"

// configs for sbt-github-packages plugin
githubOwner := "Prom3th3us"
githubRepository := "boring"
githubTokenSource := TokenSource.GitConfig("github.token")

libraryDependencies ++= List(
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-monix" % "3.3.15",
  "com.softwaremill.sttp.client3" %% "circe" % "3.3.15",
  "io.circe" %% "circe-generic" % "0.14.1"
)
// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.4.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11" % Runtime

enablePlugins(SemVerPlugin)
gitVersioningSnapshotLowerBound in ThisBuild := "0.0.1"
