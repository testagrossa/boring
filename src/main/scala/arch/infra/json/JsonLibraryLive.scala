package arch.infra.json

import arch.common.ProgramLive.App
import scala.concurrent.ExecutionContext.Implicits.global

object JsonLibraryLive extends JsonLibraryF[App]
