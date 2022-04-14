package arch.infra.router

trait ActionHandler[F[_], A <: Action] {
  def handle(a: A): F[A#ReturnType]
}
