package arch.infra.logging

import arch.common.Program
import arch.common.Program.MError
import org.slf4j.LoggerFactory
import cats.implicits._

case class LoggingF[F[_]: MError]() extends LoggingLibrary[F] {
  private val logger = LoggerFactory.getLogger("main-logger")
  override def logDebug(msg: String): Program.Context => F[Unit] =
    ctx => MError[F].unit.map(_ => logger.debug(msg, ctx))

  override def logError(msg: String): Program.Context => F[Unit] =
    ctx => MError[F].unit.map(_ => logger.error(msg, ctx))

  override def logInfo(msg: String): Program.Context => F[Unit] =
    ctx => MError[F].unit.map(_ => logger.info(msg, ctx))

  override def logWarn(msg: String): Program.Context => F[Unit] =
    ctx => MError[F].unit.map(_ => logger.warn(msg, ctx))
}
