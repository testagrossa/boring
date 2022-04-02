package arch.app

import arch.common.ProgramLive
import arch.model.services.user.UserAction

object Runner {
  import arch.common.ProgramBuilder._
  val prod = false
  def main(args: Array[String]): Unit = {
    val result = if(prod) {
      println(s"RUNNING ENV=prod")
      // DEFINITIONS
      import scala.concurrent.Await
      import scala.concurrent.duration._
      type Env[A] = ProgramLive.App[A]
      // EXECUTION
      val router = implicitly[ProgramBuilder[Env]].buildApp()
      val actionResult = router.publish(UserAction(1))
      // OUTPUT
      val scheduler = monix.execution.Scheduler.Implicits.global
      Await.result(actionResult.value.runToFuture(scheduler), 1.second)
    } else {
      println(s"RUNNING ENV=test")
      // DEFINITIONS
      type Env[A] = ProgramLive.Test[A]
      // EXECUTION
      val router = implicitly[ProgramBuilder[Env]].buildApp()
      val actionResult = router.publish(UserAction(1))
      // OUTPUT
      actionResult
    }

    println(result)
  }
}
