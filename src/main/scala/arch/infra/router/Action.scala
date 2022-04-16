package arch.infra.router

trait Action[R] {
  type ReturnType = R
}
