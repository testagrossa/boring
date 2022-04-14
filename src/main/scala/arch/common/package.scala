package arch

import cats.MonadError

package object common {
  val unknownErrorCode = -1

  type MError[F[_]] = MonadError[F, ProgramError]
  object MError {
    def apply[F[_]](implicit m: MError[F]): MError[F] = m
  }

}
