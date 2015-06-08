package tables

import models.Comment

import slick.driver.JdbcProfile

import slick.driver.H2Driver.api._

class Comments(tag: Tag) extends Table[Comment](tag, "COMMENTS"){
  val users = TableQuery[Users]
  val posts = TableQuery[Posts]

  def id      = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def content = column[String]("CONTENT")
  def userID  = column[Long]("USER_ID")
  def postID  = column[Long]("POST_ID")

  def user    = foreignKey("USER_FK2", userID, users)(_.id)
  def post    = foreignKey("POST_FK", userID, posts)(_.id)

  def * = (content, userID, postID, id.?) <> (Comment.tupled, Comment.unapply _)

}
