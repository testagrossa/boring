package arch.common

import arch.common.ProgramLive.{App, Test}
import arch.infra.json.{JsonLibraryF, JsonLibraryLive, JsonLibraryTest}
import arch.infra.router.RouterF
import arch.infra.router.RouterLive.{RouterLive, RouterTest}
import arch.model.services.RunAction.RunActionHandler
import arch.model.services.RunServiceF.{RunServiceLive, RunServiceTest}
import arch.model.services.{RunAction, RunServiceF}
import arch.model.{UserRepoF, UserRepoLive, UserRepoTest}

object ProgramBuilder {
  trait ProgramBuilder[F[_]] {
    def buildApp(): RouterF[F]
  }

  implicit lazy val production: ProgramBuilder[App] = () => {
    import ProgramLive.App
    implicit lazy val userRepoLive: UserRepoF[App] = UserRepoLive
    implicit lazy val jsonLibraryLive: JsonLibraryF[App] = JsonLibraryLive
    implicit lazy val runService: RunServiceF[App] = RunServiceLive
    implicit lazy val router: RouterF[App] = RouterLive
    router.subscribe[RunAction](new RunActionHandler[App]())
    router
  }

  implicit lazy val testing: ProgramBuilder[Test] = () => {
    import ProgramLive.Test
    implicit lazy val userRepoLive: UserRepoF[Test] = UserRepoTest
    implicit lazy val jsonLibraryLive: JsonLibraryF[Test] = JsonLibraryTest
    implicit lazy val runService: RunServiceF[Test] = RunServiceTest
    implicit lazy val router: RouterF[Test] = RouterTest
    router.subscribe[RunAction](new RunActionHandler[Test]())
    router
  }
}
