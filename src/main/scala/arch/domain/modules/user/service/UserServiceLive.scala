package arch.domain.modules.user.service

import arch.common.ProgramLive.{App, Test}
import com.typesafe.config.Config

object UserServiceLive {
  class UserServiceApp(c: Config) extends UserServiceF[App](c)
  class UserServiceTest(c: Config) extends UserServiceF[Test](c)
}
