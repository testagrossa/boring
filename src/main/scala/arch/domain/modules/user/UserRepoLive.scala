package arch.domain.modules.user

import arch.common.ProgramLive.{App, Test}

object UserRepoLive {
  object UserRepoApp extends UserRepoF[App]
  object UserRepoTest extends UserRepoF[Test]
}

