package arch.model.services.user

import arch.model.Model
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

object UserModel extends Model {
  type Id = String
  type Entity = User

  case class User(id: Id)

  object User {
    implicit val userDecoder: Decoder[User] = deriveDecoder[User]
    implicit val userEncoder: Encoder[User] = deriveEncoder[User]
    implicit val identifier: Identifiable[User] = (user: User) => user.id
  }
}
