package arch.app

import arch.common.Program.{Context, MError, ProgramError}
import arch.common.{Program, ProgramLive}
import arch.infra.json.{JsonLibraryF, JsonLibraryLive, JsonLibraryTest}
import arch.model.UserModel.User
import arch.model.{UserRepoF, UserRepoLive, UserRepoTest}
import cats.implicits._

object Runner {
  // import ProgramLive.App
  // import scala.concurrent.ExecutionContext.Implicits.global
  // implicit val userRepoLive: UserRepoF[App] = UserRepoLive
  // implicit val jsonLibraryLive: JsonLibraryF[App] = JsonLibraryLive

  import ProgramLive.Test
  implicit val userRepoLive: UserRepoF[Test] = UserRepoTest
  implicit val jsonLibraryLive: JsonLibraryF[Test] = JsonLibraryTest

  type Args = JsonLibraryLive.JsonType


  def run[F[_]: MError](args: Args)(
    implicit userRepo: UserRepoF[F], jsonLibrary: JsonLibraryF[F]
  ): F[(Option[String], Option[String], User)] = {
    val ctx = Context("run")
    val user = User("Franco")
    val userJsonFromTo = jsonLibrary.jsonFromTo(User.userDecoder, User.userEncoder)
    for {
      _ <- userRepo.set(user)
      u1 <- userRepo.get(user.id)
      u2 <- userRepo.get("Euge")
      js1 = u1.map(userJsonFromTo.from).map(jsonLibraryLive.prettyPrint)
      parsedJson <- {
        js1.map(jsonLibrary.parse) match {
          case Some(maybeParsed) => maybeParsed.flatMap(userJsonFromTo.to)
          case None => MError[F].raiseError(ProgramError((), "js1 missing", ctx))
        }
      }
      js2 = u2.map(userJsonFromTo.from)
    } yield (js1, js2, parsedJson)
  }

  def main(args: Array[String]): Unit = {
    import scala.concurrent.Await
    import scala.concurrent.duration._

    val noArgs = null
    // val awaitable = run[App](noArgs)
    // val result = Await.result(awaitable.value, 1.second)
    // println(result)
    val awaitable = run[Test](noArgs)
    println(awaitable)
  }
}
