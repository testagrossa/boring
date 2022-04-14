package arch.common

import scala.util.{Failure, Success, Try}

object Program {

  object App {
    def fromEither[F[_]: MError, E, A](
        ctx: Context
    )(errorCode: => Int = unknownErrorCode)(a: => Either[E, A]): F[A] =
      MError[F].fromEither[A](
        a match {
          case Left(e)      => Left(ProgramError.fromError(e)(ctx)(errorCode))
          case Right(value) => Right(value)
        }
      )
    def fromTry[F[_]: MError, A](
        ctx: Context
    )(errorCode: => Int = unknownErrorCode)(a: => Try[A]): F[A] =
      MError[F].fromEither[A](
        a match {
          case Failure(exception) =>
            Left(ProgramError.fromThrowable(exception)(ctx)(errorCode))
          case Success(value) => Right(value)
        }
      )
    def lift[F[_]: MError, A](ctx: Context)(
        errorCode: => Int = unknownErrorCode
    )(a: => A = ()): F[A] = fromTry(ctx)(errorCode)(Try(a))
    def apply[F[_]: MError, A](
        ctx: Context,
        errorCode: => Int = unknownErrorCode
    )(a: => A): F[A] = lift(ctx)(errorCode)(a)
    def unit[F[_]: MError](ctx: Context): F[Unit] = apply(ctx)()
  }
}
