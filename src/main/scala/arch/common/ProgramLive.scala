package arch.common

import arch.common.Program.ProgramError
import cats.data.EitherT

import scala.concurrent.Future

object ProgramLive {
  type App[A] = EitherT[Future, ProgramError, A]
  type Test[A] = Either[ProgramError, A]
}
