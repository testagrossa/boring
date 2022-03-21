package arch.infra.json

import arch.common.Program
import arch.common.Program.{Context, MError}
import io.circe.{Decoder, Encoder, Json, Printer}

class JsonLibraryF[F[_]: MError] extends JsonLibrary[F] {
  type JsonType = Json

  val ctx: Context = Context("json")
  val jsonDecodeErrorCode = 1

  class To[A](implicit d: Decoder[A]) extends JsonTo[A] {
    override def to(json: JsonType): F[A] =
      Program.App.fromEither[F, Throwable, A](ctx)(jsonDecodeErrorCode)(d.decodeJson(json))
  }
  class From[A](implicit e: Encoder[A]) extends JsonFrom[A] {
    override def from(from: A): JsonType = e.apply(from)
  }
  class JsonParserF[A](t: To[A], f: From[A]) extends JsonParser[A] {
    override def to(json: JsonType): F[A] = t.to(json)
    override def from(from: A): JsonType = f.from(from)
  }
  object PrettyPrinter extends JsonPrinter {
    override def prettyPrint(json: Json): String = Printer.spaces2.print(json)
  }
  def jsonParser[A](implicit d: Decoder[A], e: Encoder[A]): JsonParser[A] = new JsonParserF[A](new To(), new From())
  def prettyPrint(json: Json): String = PrettyPrinter.prettyPrint(json)
}
