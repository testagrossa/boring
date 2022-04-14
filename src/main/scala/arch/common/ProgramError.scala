package arch.common

case class ProgramError(
    error: Any,
    msg: String,
    ctx: Context,
    errorCode: Int = unknownErrorCode,
    stackTrace: Seq[String] = Seq.empty
)

object ProgramError {
  def fromThrowable(exception: Throwable): Context => Int => ProgramError =
    ctx =>
      errorCode => {
        val stackTrace =
          Seq.empty // exception.getStackTrace.map(_.toString).take(10)
        ProgramError(
          exception,
          "error message: " + exception.getMessage,
          ctx,
          errorCode,
          stackTrace
        )
      }
  def fromError[E](e: E): Context => Int => ProgramError = ctx =>
    errorCode => {
      ProgramError(
        e,
        "error message: " + e.toString,
        ctx,
        errorCode,
        stackTrace = Seq.empty
      )
    }
}
