package arch.model.services

import arch.common.Program.{Context, MError, ProgramError}
import arch.common.ProgramLive.{App, Test}
import arch.infra.json.{JsonLibraryF, JsonLibraryLive}
import arch.infra.router.{Action, ActionHandler}
import arch.model.UserModel.User
import arch.model.UserRepoF
import cats.implicits._
import io.circe.Json
import org.slf4j.{Logger, LoggerFactory}

trait RunService[F[_], A <: Action] {
  def run(args: A)(
    implicit userRepo: UserRepoF[F], jsonLibrary: JsonLibraryF[F] // @TODO revisit if this should go into constructor
  ): F[A#ReturnType]
}

case class RunAction(id: Int) extends Action {
  type ReturnType = (Option[String], Option[JsonLibraryLive.JsonType], User)
}

object RunAction {
  class RunActionHandler[F[_]: MError](implicit
    service: RunServiceF[F],
    userRepo: UserRepoF[F],
    jsonLibrary: JsonLibraryF[F]
  ) extends ActionHandler[F, RunAction] {
    override def handle(a: RunAction): F[(Option[String], Option[Json], User)] = service.run(a)
  }
}

class RunServiceF[F[_]: MError] extends RunService[F, RunAction] {

  def run(args: RunAction)(
    implicit userRepo: UserRepoF[F], jsonLibrary: JsonLibraryF[F]
  ): F[(Option[String], Option[JsonLibraryLive.JsonType], User)] = {
    val ctx = Context("run")
    val user = User("Franco")
    val userJsonFromTo = jsonLibrary.jsonFromTo(User.userDecoder, User.userEncoder)
    for {
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

object RunServiceF {
  import scala.concurrent.ExecutionContext.Implicits.global
  object RunServiceLive extends RunServiceF[App]
  object RunServiceTest extends RunServiceF[Test]
}
