import sbt.Keys.version

import scala.util.Try

class VersionBump(currentVersion: String) {

  def apply(arg: Option[String]): Option[Unit] =
    for {
      argument <- arg //
      done <- argument match {
        case "patch" =>
          Versioning.increasePatchVersion
        case "minor" =>
          Versioning.increaseMinorVersion
        case "major" =>
          Versioning.increaseMajorVersion
        case _ =>
          None
      }
    } yield done

  case class Version(major: Int, minor: Int, patch: Int) {
    override def toString = s"""version := "$major.$minor.$patch""""
    def majorRelease = copy(major = major + 1, minor = 0, patch = 0)
    def minorRelease = copy(minor = minor + 1, patch = 0)
    def patchRelease = copy(patch = patch + 1)
  }
  object Version {
    def apply(version: String): Option[Version] = {
      version.split('.').toList match {
        case major :: minor :: patch :: ignored =>
          for {
            major <- Try(major.toInt).toOption
            minor <- Try(minor.toInt).toOption
            patch <- Try(patch.toInt).toOption
          } yield Version(major, minor, patch)
        case _ => None
      }
    }
  }

  object Versioning {
    import java.io.PrintWriter
    private def write(filename: String, text: String) =
      new PrintWriter(filename) { write(text); close() }

    private def perform(upgrade: Version => Version): Option[Unit] =
      Version(currentVersion).map { version =>
        write("version.sbt", s"${upgrade(version)}")
      }

    def increaseMajorVersion: Option[Unit] = perform(_.majorRelease)
    def increaseMinorVersion: Option[Unit] = perform(_.minorRelease)
    def increasePatchVersion: Option[Unit] = perform(_.patchRelease)
  }

}
