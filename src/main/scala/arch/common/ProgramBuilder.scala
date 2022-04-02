package arch.common

import arch.common.ProgramLive.{App, Test}
import arch.infra.json.{JsonLibraryF, JsonLibraryLive, JsonLibraryTest}
import arch.infra.router.RouterF
import arch.infra.router.RouterLive.{RouterLive, RouterTest}
import arch.model.services.user.UserAction.UserActionHandler
import arch.model.services.user.UserServiceF.{UserServiceLive$, UserServiceTest$}
import arch.model.services.user._

object ProgramBuilder {
  trait ProgramBuilder[F[_]] {
    def buildApp(): RouterF[F]
  }

  implicit lazy val production: ProgramBuilder[App] = () => {
    import ProgramLive.App
    implicit lazy val userRepoLive: UserRepoF[App] = UserRepoLive
    implicit lazy val jsonLibraryLive: JsonLibraryF[App] = JsonLibraryLive
    implicit lazy val runService: UserServiceF[App] = UserServiceLive$
    implicit lazy val router: RouterF[App] = RouterLive
    router.subscribe[UserAction](new UserActionHandler[App]())
    router
  }

  implicit lazy val testing: ProgramBuilder[Test] = () => {
    import ProgramLive.Test
    implicit lazy val userRepoLive: UserRepoF[Test] = UserRepoTest
    implicit lazy val jsonLibraryLive: JsonLibraryF[Test] = JsonLibraryTest
    implicit lazy val runService: UserServiceF[Test] = UserServiceTest$
    implicit lazy val router: RouterF[Test] = RouterTest
    router.subscribe[UserAction](new UserActionHandler[Test]())
    router
  }
}
