package com.geoevent

import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, RootJsonFormat, enrichAny}

import java.util.UUID

case class ErrorResponse(str: String)

case class User(id: String = UUID.randomUUID().toString, name: String, phone: String, validated: Boolean)
case class Users(users: Seq[User])

object UserCalls extends UserRegistry {
  implicit val ErrorResponseJsonFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat4(User)
  implicit val usersJsonFormat: RootJsonFormat[Users] = jsonFormat1(Users)

  val userRoutes: Route =
    pathPrefix("users") {
      pathEnd {
          post {
            entity(as[User]) { user =>
              complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(user).toJson.compactPrint))
            }
         }
      } ~
        path(Segment) { id =>
          get {
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, _get(id).map(_.toJson.compactPrint).getOrElse("User not found")))
          } ~
            delete {
              complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, _delete(id) match {
                case 1 => User(id = id, name = "", phone = "", validated = false).toJson.compactPrint
                case _ => ErrorResponse("User not found").toJson.compactPrint
              }))
            } ~
            put {
              entity(as[User]) { user =>
                complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, _update(user) match {
                  case 1 => user.toJson.compactPrint
                  case _ => ErrorResponse("User not found").toJson.compactPrint
                }))
              }
            }
        }
    }
}
