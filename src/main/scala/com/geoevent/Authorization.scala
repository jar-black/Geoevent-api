package com.geoevent

import com.geoevent.registies.AuthRegistry
import org.apache.pekko.http.scaladsl.model.{HttpResponse, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directive1
import org.apache.pekko.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}

object Authorization extends AuthRegistry {
  def basicAuth: Directive1[String] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if isValidBasicAuth(token) => provide(token)
      case _ =>
        complete(HttpResponse(StatusCodes.Unauthorized, entity = "The resource requires basic authentication"))
    }
  }

  def authorizeToken: Directive1[String] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) => provide(isValidToken(token).getOrElse("Invalid token"))
      case _ =>
        complete(HttpResponse(StatusCodes.Unauthorized, entity = "The resource requires a valid token"))
    }
  }

  private def isValidBasicAuth(token: String): Boolean = {
    println(s"Basic auth token: $token")
    token == "valid-token"
  }

  private def isValidToken(token: String): Option[String] = {
    token.split(" ").toList match {
      case "Bearer" :: token :: Nil =>
        getUserIdFromToken(token).map(userId => {
          userId
        })
      case _ => None
    }
  }
}
