package com.geoevent

import org.apache.pekko.http.scaladsl.model.{HttpResponse, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directive1
import org.apache.pekko.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}

object Authorization {
  def basicAuth: Directive1[String] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if isValidBasicAuth(token) => provide(token)
      case _ =>
        complete(HttpResponse(StatusCodes.Unauthorized, entity = "The resource requires basic authentication"))
    }
  }

  def authorizeToken: Directive1[String] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if isValidToken(token) => provide(token)
      case _ =>
        complete(HttpResponse(StatusCodes.Unauthorized, entity = "The resource requires basic authentication"))
    }
  }

  private def isValidBasicAuth(token: String): Boolean = {
    println(s"Basic auth token: $token")
    token == "valid-token"
  }

  private def isValidToken(token: String): Boolean = {
    println(s"Authorize token: $token")
    token == "valid-token"
  }
}
