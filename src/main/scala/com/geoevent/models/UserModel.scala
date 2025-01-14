package com.geoevent.models

import spray.json.DefaultJsonProtocol.{BooleanJsonFormat, StringJsonFormat, jsonFormat1, jsonFormat5, seqFormat}
import spray.json.RootJsonFormat

import java.util.UUID

object UserModel {
  case class User(id: String = UUID.randomUUID().toString, name: String, phone: String, passwordHash: String, validated: Boolean)

  case class Users(users: Seq[User])

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat5(User)
  implicit val usersJsonFormat: RootJsonFormat[Users] = jsonFormat1(Users)
}
