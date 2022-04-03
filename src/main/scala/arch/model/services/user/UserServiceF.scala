package arch.model.services.user

import arch.common.Program.{Context, MError, ProgramError}
import arch.common.ProgramLive.{App, Test}
import arch.infra.json.{JsonLibraryF, JsonLibraryTest}
import arch.infra.router.{Action, ActionHandler}
import arch.model.services.user.UserConfig.UserConfigF
import arch.model.services.user.UserModel.User
import cats.implicits._
import com.typesafe.config.Config
import io.circe.Json

trait UserService[F[_], A <: Action] {
  def run(args: A)(
    implicit userRepo: UserRepoF[F], jsonLibrary: JsonLibraryF[F], userConfig: UserConfigF[F] // @TODO revisit if this should go into constructor
  ): F[A#ReturnType]
}

case class UserAction(id: Int) extends Action {
  type ReturnType = (Option[String], Option[JsonLibraryTest.JsonType], User)
}

object UserAction {
  class UserActionHandler[F[_]: MError](
    implicit
    service: UserServiceF[F],
    userRepo: UserRepoF[F],
    jsonLibrary: JsonLibraryF[F],
    userConfig: UserConfigF[F]
  ) extends ActionHandler[F, UserAction] {
    override def handle(a: UserAction): F[(Option[String], Option[Json], User)] = service.run(a)
  }
}

class UserServiceF[F[_]: MError](c: Config) extends UserService[F, UserAction] {

  def run(args: UserAction)(
    implicit userRepo: UserRepoF[F], jsonLibrary: JsonLibraryF[F], userConfig: UserConfigF[F]
  ): F[(Option[String], Option[JsonLibraryTest.JsonType], User)] = {
    val ctx = Context("run")
    val user = User("Franco")
    val userJsonFromTo = jsonLibrary.jsonFromTo[User]
    for {
      config <- userConfig.fromConfig(c)
      _ = println(s"user config = $config")
      _ <- userRepo.set(user)
      u1 <- userRepo.get(user.id)
      u2 <- userRepo.get("Euge")
      js1 = u1.map(userJsonFromTo.from).map(jsonLibrary.prettyPrint)
      parsedJson <- {
        js1.map(jsonLibrary.parse) match {
          case Some(maybeParsed) => maybeParsed.flatMap(userJsonFromTo.to)
          case None => MError[F].raiseError(ProgramError((), "js1 missing", ctx))
        }
      }
      js2 = u2.map(userJsonFromTo.from)
    } yield (js1, js2, parsedJson)
  }
}

object UserServiceF {
  class UserServiceLive(c: Config) extends UserServiceF[App](c)
  class UserServiceTest(c: Config) extends UserServiceF[Test](c)
}
