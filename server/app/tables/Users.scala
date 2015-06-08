package tables

import models.User
import slick.driver.JdbcProfile

import slick.driver.H2Driver.api._

/**
 * Created by flavio on 6/4/15.
 */
class Users(tag: Tag) extends Table[User](tag, "USERS"){
  def id    = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def email = column[String]("EMAIL")
  def password = column[String]("PASSWORD")

  def * = (email, password, id.?) <> (User.tupled, User.unapply _)

}
