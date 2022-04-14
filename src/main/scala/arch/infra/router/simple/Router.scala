package arch.infra.router.simple

trait Router[In, Out] {
  def route: In => Out
}

object Router {
  class RouterMock[In, Out](handler: In => Out) extends Router[In, Out] {
    override def route = { in =>
      handler(in)
    }
  }

  object Example {
    case class RequestJson(name: String)
    case class ResponseJson(hello: String)

    val example = new RouterMock[RequestJson, ResponseJson](handler = { in =>
      ResponseJson(s"hello, ${in.name}")
    })

    example.route(RequestJson("name"))
  }
}
