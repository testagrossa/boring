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
