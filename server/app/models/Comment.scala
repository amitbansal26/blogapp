package models

case class Comment(content: String, userID: Long, postID: Long, id: Option[Long])

object CommentJsonFormat {
  import play.api.libs.json._
  import play.api.data._
  import play.api.data.Forms._
  import models.UserJsonFormats._


  implicit val commentFormat = Json.format[Comment]

}
