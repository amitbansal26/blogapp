package models


/**
 * Created by flavio on 6/4/15.
 */
case class User (email: String, password: String, id: Option[Long])

object UserJsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val userFormat = Json.format[User]
}
