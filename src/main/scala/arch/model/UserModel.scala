package arch.model

import io.circe._
import io.circe.generic.semiauto._
import arch.infra.json.JsonLibraryLive.{jsonFromTo, JsonFromTo}
import arch.infra.json.JsonLibraryTest.{jsonFromTo, JsonFromTo}

object UserModel extends Model {
  type Id = String
  type Entity = User

  case class User(id: Id, country: String = "AR", user: Option[User] = Some(User("1", "AR", None)))
  object User {
    implicit val userDecoder: Decoder[User] = deriveDecoder[User]
    implicit val userEncoder: Encoder[User] = deriveEncoder[User]
    implicit val userJsonParser: JsonFromTo[User] = jsonFromTo[User]
    implicit val identifier: Identifiable[User] = (user: User) => user.id
  }
}

