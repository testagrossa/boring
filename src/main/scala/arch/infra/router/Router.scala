package arch.infra.router

import scala.reflect.ClassTag

trait Router[F[_]] {
  def subscribe[A <: Action[Any]: ClassTag](
      handler: A => F[A#ReturnType]
  ): Unit
  def publish[A <: Action[Any]](action: A): F[A#ReturnType]
}
