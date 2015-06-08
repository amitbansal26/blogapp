package tables

import models.Post

import slick.driver.JdbcProfile

import slick.driver.H2Driver.api._


class Posts(tag: Tag) extends Table[Post](tag, "POSTS"){
  val users = TableQuery[Users]

  def id    = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def subject = column[String]("SUBJECT")
  def content = column[String]("CONTENT")
  def userID  = column[Long]("USER_ID")
  def user    = foreignKey("USER_FK", userID, users)(_.id)

  def * = (subject, content, userID, id.?) <> (Post.tupled, Post.unapply _)
}
