package arch.infra.router

import arch.common.ProgramLive.{App, Test}
import arch.infra.logging.LoggingLibrary

object RouterLive {
  class RouterApp(implicit logger: LoggingLibrary[App]) extends RouterF[App]
  class RouterTest(implicit logger: LoggingLibrary[Test]) extends RouterF[Test]
}
