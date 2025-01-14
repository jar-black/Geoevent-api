package com.geoevent.routes

import com.geoevent.Authorization
import com.geoevent.models.ErrorModels.ErrorResponse
import com.geoevent.models.UserModel.User
import com.geoevent.registies.UserRegistry
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.enrichAny
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


object UserCalls extends UserRegistry {

  val userRoutes: Route =
    pathPrefix("users") {
      pathEnd {
          post {
            entity(as[User]) { user =>
              complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(user).toJson.compactPrint))
            }
         }
      } ~
        Authorization.authorizeToken { userId =>
          path(Segment) { id =>
            get {
              if(id == userId) {
                complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, getUserById(id).map(_.toJson.compactPrint).getOrElse("User not found")))
              } else {
                complete(StatusCodes.Forbidden, HttpEntity(ContentTypes.`application/json`, ErrorResponse("Not authorized to access this user").toJson.compactPrint))
              }
            } ~
              delete {
                complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, _delete(id) match {
                  case 1 => User(id = id, name = "", phone = "", validated = false, passwordHash = "").toJson.compactPrint
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
}
