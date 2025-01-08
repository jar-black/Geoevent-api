package com.geoevent.models

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat

object AuthModel {
  case class Credentials(username: String, password: String)
  case class Authorization(token: String, userId: String, valid_timestamp: String)

  implicit val credentialsJsonFormat: RootJsonFormat[Credentials] = jsonFormat2(Credentials)
}
