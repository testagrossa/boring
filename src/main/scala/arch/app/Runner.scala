package arch.app

import arch.common.ProgramLive
import arch.infra.json.{JsonLibraryF, JsonLibraryLive, JsonLibraryTest}
import arch.infra.router.{ActionHandler, RouterF}
import arch.infra.router.RouterF.RouterTest
import arch.model.services.RunAction.RunActionHandler
import arch.model.services.{RunAction, RunServiceF}
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
  implicit val router: RouterF[Test] = RouterTest

  router.subscribe[RunAction](new RunActionHandler[Test]())

  def main(args: Array[String]): Unit = {
    // import scala.concurrent.Await
    // import scala.concurrent.duration._

    // val awaitable = router.publish(RunAction(1))
    // val result = Await.result(awaitable.value, 1.second)
    // println(result)
    val awaitable = router.publish(RunAction(1))
    println(awaitable)
  }
}
