package arch.infra.router

trait DispatcherF[F[_]] {
  def dispatch[A <: Action[Any]](
      a: A
  )(implicit fn: A => F[A#ReturnType]): F[A#ReturnType] = {
    fn(a)
  }
}
