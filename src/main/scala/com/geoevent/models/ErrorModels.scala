package com.geoevent.models

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat1}
import spray.json.RootJsonFormat

object ErrorModels {
  case class ErrorResponse(str: String)
  case class AuthErrorResponse(string: String)

  implicit val ErrorResponseJsonFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)
  implicit val AuthErrorResponseJsonFormat: RootJsonFormat[AuthErrorResponse] = jsonFormat1(AuthErrorResponse)
}
