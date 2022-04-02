package arch.common

import arch.common.Program.ProgramError
import cats.data.EitherT
import monix.eval.Task

object ProgramLive {

  type App[A] = EitherT[Task, ProgramError, A]
  type Test[A] = Either[ProgramError, A]
}
