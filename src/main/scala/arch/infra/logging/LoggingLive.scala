package arch.infra.logging

import arch.common.ProgramLive.{App, Test}

object LoggingLive {
  object LoggingApp extends LoggingF[App]
  object LoggingTest extends LoggingF[Test]
}
