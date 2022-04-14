package arch.infra.monitoring

import arch.common.ProgramLive

object MonitoringLive {
  object MonitoringApp extends MonitoringF[ProgramLive.App]
  object MonitoringTest extends MonitoringF[ProgramLive.Test]
}