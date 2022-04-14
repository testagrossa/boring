import sbtghpackages.TokenSource.{Environment, GitConfig}

name := "boring"
scalaVersion := "2.13.3"
organization := "Prom3th3us"

githubOwner := "Prom3th3us"
githubRepository := "boring"
githubTokenSource := TokenSource.Or(
  GitConfig("github.token"),
  Environment("PUBLISH_TOKEN")
)

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.11"

libraryDependencies ++= List(
  "com.softwaremill.sttp.client3" %% "circe" % "3.3.15",
  "io.circe" %% "circe-generic" % "0.14.1"
)

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11" % Runtime
// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.4.2"

// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.36"
