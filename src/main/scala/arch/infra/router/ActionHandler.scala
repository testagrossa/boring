package arch.infra.router

trait ActionHandler[F[_], Output, A <: Action[Output]] {
  def handle(a: A): F[A#ReturnType]
}
