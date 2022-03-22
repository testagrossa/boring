package arch.infra.router

import arch.common.ProgramLive.{App, Test}
import org.slf4j.{Logger, LoggerFactory}

object RouterLive {
  import scala.concurrent.ExecutionContext.Implicits.global
  val logger: Logger = LoggerFactory.getLogger("RouterLogger")
  object RouterLive extends RouterF[App](logger)
  object RouterTest extends RouterF[Test](logger)
}
