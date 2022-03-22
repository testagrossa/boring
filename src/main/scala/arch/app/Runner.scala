package arch.app

import arch.common.ProgramLive
import arch.infra.json.{JsonLibraryF, JsonLibraryLive, JsonLibraryTest}
import arch.model.services.RunServiceF
import arch.model.services.RunServiceF.RunServiceTest
import arch.model.{UserRepoF, UserRepoLive, UserRepoTest}
// import cats.implicits._

object Runner {
  // import ProgramLive.App
  // import scala.concurrent.ExecutionContext.Implicits.global
  // implicit val userRepoLive: UserRepoF[App] = UserRepoLive
  // implicit val jsonLibraryLive: JsonLibraryF[App] = JsonLibraryLive

  import ProgramLive.Test
  implicit val userRepoLive: UserRepoF[Test] = UserRepoTest
  implicit val jsonLibraryLive: JsonLibraryF[Test] = JsonLibraryTest
  implicit val runService: RunServiceF[Test] = RunServiceTest

  def main(args: Array[String]): Unit = {
    // import scala.concurrent.Await
    // import scala.concurrent.duration._

    val noArgs = null
    // val awaitable = run[App](noArgs)
    // val result = Await.result(awaitable.value, 1.second)
    // println(result)
    val awaitable = runService.run(noArgs)
    println(awaitable)
  }
}
