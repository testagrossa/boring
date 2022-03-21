package arch.app

import arch.common.Program.MError
import arch.common.ProgramLive
import arch.infra.json.{JsonLibraryF, JsonLibraryLive}
import arch.model.UserModel.User
import arch.model.{UserRepoF, UserRepoLive}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.implicits._

object Runner {
  import ProgramLive.App
  implicit val userRepoLive: UserRepoF[App] = UserRepoLive
  implicit val jsonLibraryLive: JsonLibraryF[App] = JsonLibraryLive
  type Args = JsonLibraryLive.JsonType


  def run[F[_]: MError](args: Args)(
    implicit userRepo: UserRepoF[F], jsonLibrary: JsonLibraryF[F]
  ): F[(Option[String], Option[String])] = {
    val user = User("Franco")
    for {
      _ <- userRepo.set(user)
      u1 <- userRepo.get(user.id)
      u2 <- userRepo.get("Euge")
      js1 = u1.map(jsonLibrary.jsonParser(User.userDecoder, User.userEncoder).from).map(jsonLibraryLive.prettyPrint)
      js2 = u2.map(jsonLibrary.jsonParser(User.userDecoder, User.userEncoder).from).map(jsonLibraryLive.prettyPrint)
    } yield js1 -> js2
  }

  def main(args: Array[String]): Unit = {
    val noArgs = null
    val awaitable = run[App](noArgs)
    val result = Await.result(awaitable.value, 1.second)
    println(result)
  }
}
