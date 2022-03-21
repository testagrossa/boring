package arch.model

import arch.common.ProgramLive.App
import scala.concurrent.ExecutionContext.Implicits.global

object UserRepoLive extends UserRepoF[App]

