package arch.model

trait Repo[F[_]] {
  type M <: Model
  def set(a: M#Entity)(implicit id: M#Identifiable[M#Entity]): F[Unit]
  def get(id: M#Id): F[Option[M#Entity]]
}
