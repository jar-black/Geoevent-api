package com.geoevent

import com.geoevent.models.AuthModel.{Authorization, Credentials, _}
import com.geoevent.models.UserModel.User
import com.geoevent.routes.{AuthCalls, GeoStampCalls, UserCalls}
import org.apache.pekko
import org.apache.pekko.actor.testkit.typed.scaladsl.ActorTestKit
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model.headers.OAuth2BearerToken
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID
import scala.util.Random

trait TestFrame extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest with BeforeAndAfterAll {
  var testUser: User = _
  var bearerTokenTestUser: OAuth2BearerToken = _
  val routes: Route = UserCalls.userRoutes ~ AuthCalls.authorizationRoutes ~ GeoStampCalls.geoStampsRoutes

  private val userId: String = UUID.randomUUID().toString
  private val phoneNumber: String = (new Random).nextInt(100000000).toString
  private val credentials: Credentials = Credentials(phoneNumber, "test_password")
  private val credentialsEntity: MessageEntity = Marshal(credentials).to[MessageEntity].futureValue

  private lazy val testKit: ActorTestKit = ActorTestKit()

  override def createActorSystem(): pekko.actor.ActorSystem = testKit.system.classicSystem

  private implicit def typedSystem: ActorSystem[_] = testKit.system


  override def beforeAll(): Unit = {
    println("beforeAll")
    createTestUser()
  }

  override def afterAll(): Unit = {
    println("afterAll")
    removeTestUser()
  }

  private def removeTestUser(): Unit = {
    val req = Delete(s"/users/$userId").addCredentials(bearerTokenTestUser)
    req ~> routes ~> check {
      status should be(StatusCodes.OK)
    }
  }

  private def createTestUser(): Unit = {
    testUser = User(
      id = userId,
      name = "test_name",
      phone = phoneNumber,
      passwordHash = "test_password",
      validated = false
    )
    val userEntity = Marshal(testUser).to[MessageEntity].futureValue
    val reqUser = Post("/users").withEntity(userEntity)

    testUser = reqUser ~> routes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      entityAs[User]
    }

    val reqAuth = Post("/auth").withEntity(credentialsEntity)

    bearerTokenTestUser = reqAuth ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      OAuth2BearerToken(entityAs[Authorization].token)
    }
  }
}

