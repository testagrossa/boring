package arch.infra.json

import arch.common.ProgramLive.{App, Test}
import scala.concurrent.ExecutionContext.Implicits.global

object JsonLibraryLive extends JsonLibraryF[App]
object JsonLibraryTest extends JsonLibraryF[Test]
