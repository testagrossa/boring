package arch.domain.modules.user

import arch.common.Program.MError
import arch.common.ProgramLive.{App, Test}
import arch.infra.config.ConfigF
import cats.implicits._
import com.typesafe.config.Config

case class UserConfig(flag: Boolean, value: Option[Int])

object UserConfig {
  class UserConfigF[F[_]: MError] extends ConfigF[UserConfig, F] {
    override def fromConfig(config: Config): F[UserConfig] = {
      for {
        flag <- ConfigF.parse[Boolean, F](config)(_.getBoolean("flag"))
        value <- ConfigF.parseOpt[Int, F](config)(_.getInt("value"))
      } yield UserConfig(flag, value)
    }
  }

  object UserConfigLive extends UserConfigF[App]
  object UserConfigTest extends UserConfigF[Test]
}