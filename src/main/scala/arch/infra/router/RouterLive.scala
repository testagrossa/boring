package arch.infra.router

import arch.common.ProgramLive.{App, Test}
import org.slf4j.{Logger, LoggerFactory}

object RouterLive {
  val logger: Logger = LoggerFactory.getLogger("RouterLogger")
  object RouterApp extends RouterF[App](logger)
  object RouterTest extends RouterF[Test](logger)
}
