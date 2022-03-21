package others

object Program {
  import scala.util.{Try, Success, Failure}

  import scala.concurrent.Future // import cats.Id
  import scala.concurrent.ExecutionContext.Implicits.global

  import cats.data.EitherT

  val unknownErrorCode = 0

  case class Context(name: String, conf: Map[String, Any] = Map.empty)
  case class ProgramError(error: Any, msg: String, ctx: Context, errorCode: Int = unknownErrorCode, stackTrace: Seq[String] = Seq.empty)
  object ProgramError {
    def fromThrowable(exception: Throwable): Context => Int => ProgramError = ctx => errorCode => {
      val stackTrace = Seq.empty // exception.getStackTrace.map(_.toString).take(10)
      ProgramError(exception, "error message: " + exception.getMessage, ctx, errorCode, stackTrace)
    }
    def fromError[E](e: E): Context => Int => ProgramError = ctx => errorCode => {
      ProgramError(e, "error message: " + e.toString, ctx, errorCode, stackTrace = Seq.empty)
    }
  }
  type App[A] = EitherT[Future, ProgramError, A] // @TODO change effect Id

  object App {
    def fromEither[F[_], E, A](ctx: Context)(errorCode: => Int = unknownErrorCode)(a: => Either[E, A]): App[A] = EitherT.fromEither(
      a match {
        case Left(e) => Left(ProgramError.fromError(e)(ctx)(errorCode))
        case Right(value) => Right(value)
      }
    )
    def fromTry[F[_], A](ctx: Context)(errorCode: => Int = unknownErrorCode)(a: => Try[A]): App[A] = EitherT.fromEither(
      a match {
        case Failure(exception) => Left(ProgramError.fromThrowable(exception)(ctx)(errorCode))
        case Success(value) => Right(value)
      }
    )
    def lift[F[_], A](ctx: Context)(errorCode: => Int = unknownErrorCode)(a: => A = ()): App[A] = fromTry(ctx)(errorCode)(Try(a))
    def apply[A](ctx: Context, errorCode: => Int = unknownErrorCode)(a: => A): App[A] = lift(ctx)(errorCode)(a)
    def unit(ctx: Context): App[Unit] = apply(ctx)()
  }
}

import others.Program._

trait JsonLibrary {
  type JsonType

  trait JsonPrinter {
    def prettyPrint(json: JsonType): String
  }
  trait JsonTo[A] {
    def to(json: JsonType): App[A]
  }
  trait JsonFrom[A] {
    def from(from: A): JsonType
  }
  trait JsonParser[A] extends JsonTo[A] with JsonFrom[A]
}

object JsonLibraryLive extends JsonLibrary {
  import io.circe.{Decoder, Encoder, Json}

  type JsonType = Json

  val ctx: Context = Context("json")
  val jsonDecodeErrorCode = 1

  class To[A](implicit d: Decoder[A]) extends JsonTo[A] {
    override def to(json: JsonType): App[A] = App.fromEither(ctx)(jsonDecodeErrorCode)(d.decodeJson(json))
  }
  class From[A](implicit e: Encoder[A]) extends JsonFrom[A] {
    override def from(from: A): JsonType = e.apply(from)
  }
  class JsonParserLive[A](t: To[A], f: From[A]) extends JsonParser[A] {
    override def to(json: JsonType): App[A] = t.to(json)
    override def from(from: A): JsonType = f.from(from)
  }
  def jsonParser[A](implicit d: Decoder[A], e: Encoder[A]): JsonParser[A] = new JsonParserLive[A](new To(), new From())
}

object RepoLibrary {
  type Id = String
  trait Identifiable[Entity] {
    def id(a: Entity): Id
  }
  trait Repo[Entity] {
    def set(a: Entity)(implicit id: Identifiable[Entity]): App[Unit]
    def get(id: Id): App[Option[Entity]]
  }
}

object UseModule {
  import io.circe._
  import io.circe.generic.semiauto._
  import others.RepoLibrary._
  import others.JsonLibraryLive._

  case class User(id: Id)
  object User {
    implicit val identifier: Identifiable[User] = (user: User) => user.id
    implicit val userDecoder: Decoder[User] = deriveDecoder[User]
    implicit val userEncoder: Encoder[User] = deriveEncoder[User]
    implicit val userJsonParser: JsonParser[User] = jsonParser[User]
  }
  object UserRepo extends Repo[User] {
    private val module = Context("user")
    private val userRepoErrorCode = 1
    private var users: Map[Id, User] = Map.empty[Id, User]
    override def set(a: User)(implicit id: Identifiable[User]): App[Unit] = App(module, userRepoErrorCode) {
      users = users.updated(id.id(a), a)
    }
    override def get(id: Id): App[Option[User]] = App(module, userRepoErrorCode) {
      users.get(id)
    }
  }
}

object Runner {
  import cats.data.EitherT
  import UseModule.{User, UserRepo}

  import scala.concurrent.{Future, Await} // import  cats.Id[A]
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global


  type Args = JsonLibraryLive.JsonType
  def run(args: Args): EitherT[Future, ProgramError, (Option[User], Option[User])] = {
    val user = User("Franco")
    for {
      _ <- UserRepo.set(user)
      u1 <- UserRepo.get(user.id)
      u2 <- UserRepo.get("Euge")
    } yield u1 -> u2
  }

  def main(args: Array[String]): Unit = {
    val noArgs = null
    val result: Either[ProgramError, (Option[User], Option[User])] = Await.result(run(noArgs).value, 1.second)
    println(result)
  }
}
