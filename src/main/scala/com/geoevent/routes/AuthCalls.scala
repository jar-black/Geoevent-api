package com.geoevent.routes

import com.geoevent.encrypt.Encrypt
import com.geoevent.models.AuthModel._
import com.geoevent.models.ErrorModels.AuthErrorResponse
import com.geoevent.models.UserModel.User
import com.geoevent.registies.{AuthRegistry, UserRegistry}
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.enrichAny


class AuthCalls extends AuthRegistry with UserRegistry {
  val authorizationRoutes: Route =
    pathPrefix("authorize") {
      path(Segment) { id =>
        post {
          entity(as[Credentials]) { auth =>
            val maybeUser: Option[User] = _get(auth.username)
            maybeUser.map(user => {
                if (Encrypt.validateHash(auth.password, user.passwordHash)) {
                  getAuthToken(id).map(token =>
                      complete(token.toJson.compactPrint))
                    .getOrElse {
                      createAuthToken(id).map(token =>
                          complete(token.toJson.compactPrint))
                        .getOrElse(complete(AuthErrorResponse("Authorization failed").toJson.compactPrint))
                      }
                } else {
                  complete(AuthErrorResponse("Authorization failed").toJson.compactPrint)
                }
              })
              .getOrElse(complete(AuthErrorResponse("Authorization failed").toJson.compactPrint))
          }
        }
      }
    }
}
