package controllers

import javax.inject.Inject

import models.{User, Post, Comment}
import models.UserJsonFormats._
import models.PostJsonFormats._

import tables.{Users, Posts, Comments}

import play.api.i18n.I18nSupport

import play.api._
import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc._
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

import play.api.i18n.Messages.Implicits._

import play.api.Play.current


class Application @Inject()(dbConfigProvider: DatabaseConfigProvider) extends Controller {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.driver.api._

  val users    = TableQuery[Users]
  val posts    = TableQuery[Posts]
  val comments = TableQuery[Comments]

  val postForm = Form(
    tuple(
      "subject" -> nonEmptyText,
      "content" -> nonEmptyText
    )
  )

  val commentForm = Form(
    tuple(
      "comment" -> nonEmptyText,
      "postId" -> longNumber
    )
  )

  val signupForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "id" -> optional(longNumber)
    )(User.apply)(User.unapply)
  )


  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def isAuthenticated = Action { implicit request =>
    request.session.get("username").map { username =>
      val message = Map("message" -> "User is logged in already",
        "user"-> username)
      Ok(Json.toJson(Map("success"->message)))
    }.getOrElse {
      Unauthorized("Oops, you are not connected")
    }
  }

  def logout = Action { request =>
    Ok(buildJsonResponse("success", "Logged out successfully")).withNewSession
  }

  def login = Action.async { implicit request =>
    signupForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      user => {
        val query = for {
          u <- users if u.email === user.email && u.password === user.password
        } yield(u.email)
        val u = query.result
        val f: Future[Seq[String]] = dbConfig.db.run(u)
        f.map { s =>
          if(s.isEmpty){
            BadRequest(buildJsonResponse("error", "Incorrect email or password"));
          } else {
            val message = Map("message"->"Logged in successfully", "user"->s.headOption.getOrElse(""))
            val json = Json.toJson(Map("success"->message))
            Ok(json).withSession("username"->s.headOption.getOrElse(""))
          }
        }
      }
    )
  }

  def signup = Action.async { implicit request =>
    signupForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      user => {
        val resultingUsers: Future[Seq[User]] = dbConfig.db.run(users.filter(_.email === user.email).result)
        resultingUsers.map {
          case Seq(u) => BadRequest(buildJsonResponse("error","User exists"))
          case _ => {
            dbConfig.db.run(users += user )
            Ok(buildJsonResponse("success", "User created successfully"))
              .withSession("username"->user.email)
          }
        }
      }
    )
  }

  def getPosts = Action.async {
    val query = for{
      p <- posts
      u <- users if p.userID === u.id
    } yield(p, u)
    val futureResult  = dbConfig.db.run(query.result)
    futureResult.map{ data =>
      Ok(Json.toJson(data))
    }
  }

  def getUserPosts = Action.async  { implicit request =>
    val username = request.session.get("username").get

    val query = for {
      u <- users if u.email === username
      p <- posts if p.userID === u.id
    } yield(p, u)

    val futurePost = dbConfig.db.run(query.result)

    futurePost.map{ post =>
      Ok(Json.toJson(post))

    }

  }

  def getPost(id: Long) = Action.async {
    val query = for {
      p <- posts if p.id === id
      u <- users if p.userID === u.id
    } yield(p, u)

    val futurePost = dbConfig.db.run(query.result.headOption)

    futurePost.map{ post =>
      Ok(Json.toJson(post))

    }

  }

  def addComment = Action.async { implicit request =>
    commentForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      data => {
        val username = request.session.get("username")
        val u: Future[Option[User]] = findUser(username.getOrElse(""))
        u.map{ user =>
          val userID = user.get.id
          val c = Comment(data._1, userID.get, data._2, None)
          dbConfig.db.run(comments+=c)
          Ok(buildJsonResponse("success", "Comment added successfully"))
        }
      }
    )
  }

  def addPost = Action.async { implicit request =>
    postForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      data => {
        val username = request.session.get("username")
        val u: Future[Option[User]] = findUser(username.getOrElse(""))
        u.map{ user =>
          val userAuthenticated = user.get
          val userID = userAuthenticated.id.getOrElse(0L)
          val p = Post(data._1, data._2, userID, None)
          dbConfig.db.run(posts+=p)
          Ok(buildJsonResponse("success", "Post added successfully"))
        }
      }
    )
  }

  private def findUser(email: String): Future[Option[User]] = {
    val result: Future[Option[User]] = dbConfig.db.run((users.filter(_.email === email)).result.headOption)
    result
  }

  private def buildJsonResponse(key: String, message: String) = {
    Json.toJson(Map(key -> Map("message"-> message)))
  }

}
