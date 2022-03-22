package arch.infra.router

import scala.reflect.ClassTag

trait Router[F[_]] {
  def subscribe[A <: Action: ClassTag](handler: ActionHandler[F, A]): Unit
  def publish[A <: Action](action: A): F[A#ReturnType]
}
