package arch.infra.logging

import arch.common.ProgramLive.{App, Test}

object LoggingLive {
  object LoggingLiveLive extends LoggingF[App]
  object LoggingLiveTest extends LoggingF[Test]
}
