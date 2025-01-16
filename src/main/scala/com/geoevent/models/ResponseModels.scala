package com.geoevent.models

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat1}
import spray.json.RootJsonFormat

object ResponseModels {
  case class SuccessResponse(successMsg: String)
  case class ErrorResponse(errorMsg: String)
  case class AuthErrorResponse(errorMsg: String)

  implicit val SuccessResponseJsonFormat: RootJsonFormat[SuccessResponse] = jsonFormat1(SuccessResponse)
  implicit val ErrorResponseJsonFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)
  implicit val AuthErrorResponseJsonFormat: RootJsonFormat[AuthErrorResponse] = jsonFormat1(AuthErrorResponse)
}
