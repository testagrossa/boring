import io.circe.generic.auto._
import monix.eval.Task
import sttp.client3._
import sttp.client3.asynchttpclient.monix._
import sttp.client3.circe._

object HttpClient {

  case class Response[A](data: A)

  case class Query(query: String)

  def postTask[A](uri: String, bearer: String, body: String)(fn: A => Task[Unit])(implicit d: io.circe.Decoder[A]): Task[Unit] =
    AsyncHttpClientMonixBackend().flatMap { backend =>
      val r = basicRequest
        .post(uri"$uri")
        .contentType("application/json")
        .auth.bearer(bearer)
        .body(Query(body))
        .response(asJson[Response[A]].getRight)

      r.send(backend)
        .flatMap { response =>
          fn(response.body.data)
        }
        .guarantee(backend.close())
    }

  def postResponse[A](uri: String, bearer: String, body: String)(implicit d: io.circe.Decoder[A]): Task[A] =
    AsyncHttpClientMonixBackend().flatMap { backend =>
      val r = basicRequest
        .post(uri"$uri")
        .contentType("application/json")
        .auth.bearer(bearer)
        .body(Query(body))
        .response(asJson[Response[A]].getRight)

      r.send(backend)
        .flatMap { response =>
          Task(response.body.data)
        }
        .guarantee(backend.close())
    }
}