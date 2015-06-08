package models

case class Post(subject: String, content: String, userID: Long, id:Option[Long])

object PostJsonFormats {

  import play.api.libs.json._
  import play.api.data._
  import play.api.data.Forms._
  import models.UserJsonFormats._


  implicit val postFormat = Json.format[Post]

  implicit val writer = new Writes[(Post, User)] {
    def writes(t: (Post, User)): JsValue = {
      val post:Post = t._1
      val user:User = t._2
      Json.obj(
        "subject" -> post.subject,
        "content" -> post.content,
        "user" -> Json.obj(
          "email" -> user.email
        ),
        "id" -> post.id
      )
    }
  }
  /*
  implicit val postAndUserWriter = new Writes [Seq[(Post, User)]] {
    def writes(t: Seq[(Post, User)]): JsValue = {

      val a = t.head
      val post:Post = a._1
      val user:User = a._2
      Json.obj(
        "subject" -> post.subject,
        "content" -> post.content,
        "user" -> Json.obj(
          "email" -> user.email
        ),
        "id" -> post.id
      )
    }
  }*/

}
