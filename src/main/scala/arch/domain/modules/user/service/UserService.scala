package arch.domain.modules.user.service

import arch.domain.modules.user.UserConfig.UserConfigF
import arch.domain.modules.user.UserRepoF
import arch.infra.json.JsonLibraryF
import arch.infra.monitoring.MonitoringLibrary
import arch.infra.router.Action

trait UserService[F[_], A <: Action] {
  def run(args: A)(
    // @TODO revisit if this should go into constructor
    implicit userRepo: UserRepoF[F],
    jsonLibrary: JsonLibraryF[F],
    userConfig: UserConfigF[F],
    monitoring: MonitoringLibrary[F]
  ): F[A#ReturnType]
}
