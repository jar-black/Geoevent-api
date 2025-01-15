package com.geoevent

import com.geoevent.models.UserModel.User
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._

import scala.util.Random

class UserRoutesTest extends TestFrame {
  "be able to get users (GET /user)" in {
    val request = Get(uri = s"/users/${testUser.id}").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[String] should include("test_name")
    }
  }

  "be able to update users (PUT /users" in {
    val user = User(
      id = testUser.id,
      name = "test_name_updated",
      phone = (new Random).nextInt(100000000).toString,
      validated = false,
      passwordHash = "test_password")

    val userEntity = Marshal(user).to[MessageEntity].futureValue
    val request = Put(uri = s"/users/${testUser.id}").withEntity(userEntity).addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[User].name should be("test_name_updated")
    }
  }

  "be able to remove users (DELETE /users)" in {
    val request = Delete(uri = s"/users/${testUser.id}").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[User].id should be(testUser.id)
    }
  }
}

