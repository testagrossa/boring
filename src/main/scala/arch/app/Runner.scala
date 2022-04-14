package arch.app

import arch.common.{Program, ProgramLive}
import arch.domain.modules.user.model.UserModel
import arch.domain.modules.user.service.UserAction
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.Json

object Runner {
  import arch.common.ProgramBuilder._
  import scala.jdk.CollectionConverters._

  val prod = true
  val config: Config = ConfigFactory.parseMap(
    Map(
      "user.flag" -> true,
      "user.value" -> 1
    ).asJava
  )

  def main(args: Array[String]): Unit = {
    val result = if (prod) {
      println(s"RUNNING ENV=prod")
      // DEFINITIONS
      import scala.concurrent.Await
      import scala.concurrent.duration._
      type Env[A] = ProgramLive.App[A]
      // EXECUTION
      val router = implicitly[ProgramBuilder[Env]].buildApp(config)
      val actionResult = router.publish(UserAction(1))
      // OUTPUT
      actionResult.value.unsafeRunSync()
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
