package com.geoevent

import com.geoevent.UserCalls.userJsonFormat
import org.apache.pekko
import org.apache.pekko.actor.testkit.typed.scaladsl.ActorTestKit
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID
import scala.util.Random

class UserRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  lazy val testKit: ActorTestKit = ActorTestKit()

  implicit def typedSystem: ActorSystem[_] = testKit.system

  override def createActorSystem(): pekko.actor.ActorSystem =
    testKit.system.classicSystem

  lazy val routes: Route = UserCalls.userRoutes
    var responseId: String = _

      "be able to add users (POST /users)" in {
      val user = User(
        id = UUID.randomUUID().toString,
        name = "test_name",
        phone = (new Random).nextInt(100000000).toString,
        validated = false)
      val userEntity = Marshal(user).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      val request = Post("/users").withEntity(userEntity)

      responseId = request ~> routes ~> check {
        status should be(StatusCodes.Created)
        contentType should be(ContentTypes.`application/json`)
        entityAs[User].id
      }
    }

    "be able to get users (GET /user)" in {
      val request = Get(uri = s"/users/$responseId")

      request ~> routes ~> check {
        status should be(StatusCodes.OK)
        contentType should be(ContentTypes.`application/json`)
        entityAs[String] should include("test_name")
      }
    }

  "be able to update users (PUT /users" in {
    val user = User(
      id = responseId,
      name = "test_name_updated",
      phone = (new Random).nextInt(100000000).toString,
      validated = false)

    val userEntity = Marshal(user).to[MessageEntity].futureValue
    val request = Put(uri = s"/users/$responseId").withEntity(userEntity)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[User].name should be("test_name_updated")
    }
  }

    "be able to remove users (DELETE /users)" in {
      val request = Delete(uri = s"/users/$responseId")

      request ~> routes ~> check {
        status should be(StatusCodes.OK)
        contentType should be(ContentTypes.`application/json`)
        entityAs[User].id should be(responseId)
      }
    }
}
