package com.geoevent.routes

import com.geoevent.Authorization
import com.geoevent.models.ResponseModels.{ErrorResponse, SuccessResponse}
import com.geoevent.models.UserModel.{User, UserRegistration}
import com.geoevent.registies.UserRegistry
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.enrichAny

import java.util.UUID


object UserCalls extends UserRegistry {

  val userRoutes: Route =
    pathPrefix("users") {
      pathEnd {
        post {
          entity(as[UserRegistration]) { registration =>
            // Create User object with password in passwordHash field (will be hashed in registry)
            val user = User(
              id = UUID.randomUUID.toString,
              name = registration.name,
              phone = registration.phoneNumber,
              passwordHash = registration.password, // Plain password - will be hashed by _create
              validated = false
            )
            complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(user).toJson.compactPrint))
          }
        }
      } ~
        Authorization.authorizeToken { userId =>
          path(Segment) { id =>
            get {
              if (id == userId) {
                complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, getUserById(id).map(_.toJson.compactPrint).getOrElse("User not found")))
              } else {
                complete(StatusCodes.Forbidden, HttpEntity(ContentTypes.`application/json`, ErrorResponse("Not authorized to access resource").toJson.compactPrint))
              }
            } ~
              delete {
                if (id != userId) {
                  complete(StatusCodes.Forbidden, HttpEntity(ContentTypes.`application/json`, ErrorResponse("Not authorized to access resource").toJson.compactPrint))
                } else _delete(id) match {
                  case 1 => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, SuccessResponse("Successfully deleted user").toJson.compactPrint))
                  case _ => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ErrorResponse("User not found").toJson.compactPrint))
                }
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
