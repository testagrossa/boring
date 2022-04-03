package arch.app

import arch.common.ProgramLive
import arch.domain.modules.user.service.UserAction
import com.typesafe.config.{Config, ConfigFactory}

object Runner {
  import arch.common.ProgramBuilder._
  import scala.jdk.CollectionConverters._

  val prod = false
  val config: Config = ConfigFactory.parseMap(Map(
    "user.flag" -> true,
    "user.value" -> 1
  ).asJava)

  def main(args: Array[String]): Unit = {
    val result = if(prod) {
      println(s"RUNNING ENV=prod")
      // DEFINITIONS
      import scala.concurrent.Await
      import scala.concurrent.duration._
      type Env[A] = ProgramLive.App[A]
      // EXECUTION
      val router = implicitly[ProgramBuilder[Env]].buildApp(config)
      val actionResult = router.publish(UserAction(1))
      // OUTPUT
      val scheduler = monix.execution.Scheduler.Implicits.global
      Await.result(actionResult.value.runToFuture(scheduler), 1.second)
    } else {
      println(s"RUNNING ENV=test")
      // DEFINITIONS
      type Env[A] = ProgramLive.Test[A]
      // EXECUTION
      val router = implicitly[ProgramBuilder[Env]].buildApp(config)
      val actionResult = router.publish(UserAction(1))
      // OUTPUT
      actionResult
    }

    println(result)
  }
}
