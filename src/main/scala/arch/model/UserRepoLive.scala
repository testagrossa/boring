package arch.model

import arch.common.ProgramLive.{App, Test}

import scala.concurrent.ExecutionContext.Implicits.global

object UserRepoLive extends UserRepoF[App]
object UserRepoTest extends UserRepoF[Test]

