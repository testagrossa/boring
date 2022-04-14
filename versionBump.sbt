import sbt._
import sbt.inputKey
import complete.DefaultParsers._

val versionBump = inputKey[Unit](""" 
    |ie.: sbt "versionBump major" 
    |ie.: sbt "versionBump minor" 
    |ie.: sbt "versionBump patch" 
    |""".stripMargin)
versionBump := {
  new VersionBump(
    currentVersion = version.value
  ).apply(
    arg = spaceDelimited("<arg>").parsed.headOption
  )
}
