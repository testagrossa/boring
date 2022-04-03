package arch.common

import arch.common.Program.ProgramError
import cats.data.EitherT
import monix.eval.Task

object ProgramLive {

  // each type should be a subtype of MonadError
  type App[A] = EitherT[Task, ProgramError, A]
  type Test[A] = Either[ProgramError, A]
}
