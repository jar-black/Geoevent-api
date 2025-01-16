package com.geoevent.routes

import com.geoevent.encrypt.Hashing
import com.geoevent.models.AuthModel._
import com.geoevent.models.ResponseModels.AuthErrorResponse
import com.geoevent.registies.{AuthRegistry, UserRegistry}
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.enrichAny


object AuthCalls extends AuthRegistry with UserRegistry {
  val authorizationRoutes: Route =
    pathPrefix("auth") {
      post {
        entity(as[Credentials]) { cred =>
          _get(cred.phoneNumber).map(user => {
            if (Hashing.validateHash(cred.password, user.passwordHash)) {
              getValidToken(user.id).map(auth => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, auth.toJson.compactPrint)))
                .getOrElse(complete(AuthErrorResponse("Authorization failed").toJson.compactPrint))
            } else {
              complete(AuthErrorResponse("Authorization failed").toJson.compactPrint)
            }
          }).getOrElse(complete(AuthErrorResponse("Authorization failed").toJson.compactPrint))
        }
      }
    }
}
