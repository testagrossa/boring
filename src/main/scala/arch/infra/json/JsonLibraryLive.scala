package arch.infra.json

import arch.common.ProgramLive.{App, Test}

object JsonLibraryLive extends JsonLibraryF[App]
object JsonLibraryTest extends JsonLibraryF[Test]
