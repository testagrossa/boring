package arch.domain.modules.user.service

import arch.common.Program.MError
import arch.domain.modules.user.UserConfig.UserConfigF
import arch.domain.modules.user.UserRepoF
import arch.domain.modules.user.model.UserModel.User
import arch.infra.json.{JsonLibraryF, JsonLibraryTest}
import arch.infra.monitoring.MonitoringLibrary
import arch.infra.router.{Action, ActionHandler}
import io.circe.Json

case class UserAction(id: Int) extends Action {
  type ReturnType = (Option[String], Option[JsonLibraryTest.JsonType], User)
}

object UserAction {
  class UserActionHandler[F[_]: MError](
   implicit
   service: UserServiceF[F],
   userRepo: UserRepoF[F],
   jsonLibrary: JsonLibraryF[F],
   userConfig: UserConfigF[F],
   monitoring: MonitoringLibrary[F]
 ) extends ActionHandler[F, UserAction] {
    override def handle(a: UserAction): F[(Option[String], Option[Json], User)] = service.run(a)
  }
}
