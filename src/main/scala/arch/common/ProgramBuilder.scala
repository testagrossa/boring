package arch.common

import arch.common.ProgramLive.{App, Test}
import arch.infra.json.{JsonLibraryF, JsonLibraryLive, JsonLibraryTest}
import arch.infra.monitoring.MonitoringLibrary
import arch.infra.monitoring.MonitoringLive.{MonitoringLive, MonitoringTest}
import arch.infra.router.RouterF
import arch.infra.router.RouterLive.{RouterLive, RouterTest}
import arch.model.services.user.UserAction.UserActionHandler
import arch.model.services.user.UserConfig.{UserConfigF, UserConfigLive, UserConfigTest}
import arch.model.services.user.UserServiceF.{UserServiceLive, UserServiceTest}
import arch.model.services.user._
import com.typesafe.config.Config

object ProgramBuilder {
  trait ProgramBuilder[F[_]] {
    def buildApp(config: Config): RouterF[F]
  }

  implicit lazy val production: ProgramBuilder[App] = (config: Config) => {
    import ProgramLive.App
    implicit lazy val userRepoLive: UserRepoF[App] = UserRepoLive
    implicit lazy val jsonLibraryLive: JsonLibraryF[App] = JsonLibraryLive
    implicit lazy val monitoring: MonitoringLibrary[App] = MonitoringLive
    implicit lazy val userConfigF: UserConfigF[App] = UserConfigLive
    implicit lazy val runService: UserServiceF[App] = new UserServiceLive(config.getConfig("user"))
    implicit lazy val router: RouterF[App] = RouterLive
    router.subscribe[UserAction](new UserActionHandler[App]())
    router
  }

  implicit lazy val testing: ProgramBuilder[Test] = (config: Config) => {
    import ProgramLive.Test
    implicit lazy val userRepoLive: UserRepoF[Test] = UserRepoTest
    implicit lazy val jsonLibraryLive: JsonLibraryF[Test] = JsonLibraryTest
    implicit lazy val monitoring: MonitoringLibrary[Test] = MonitoringTest
    implicit lazy val userConfigF: UserConfigF[Test] = UserConfigTest
    implicit lazy val runService: UserServiceF[Test] = new UserServiceTest(config.getConfig("user"))
    implicit lazy val router: RouterF[Test] = RouterTest
    router.subscribe[UserAction](new UserActionHandler[Test]())
    router
  }
}
