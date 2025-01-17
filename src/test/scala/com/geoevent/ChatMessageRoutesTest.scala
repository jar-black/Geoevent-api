package com.geoevent

import com.geoevent.models.ChatMessageModel.ChatMessage
import com.geoevent.models.ResponseModels.SuccessResponse
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import spray.json.DefaultJsonProtocol.seqFormat

import java.sql.Timestamp

class ChatMessageRoutesTest extends TestFrame {
  var chatMessageId: String = _

  "be able to add ChatMessage (POST /msg)" in {
    val chatMessage = ChatMessage(
      id = "any",
      userId = testUser.id,
      eventId = "any",
      message = "my message",
      timestamp = new Timestamp(System.currentTimeMillis())
    )
    val chatMessageEntity = Marshal(chatMessage).to[MessageEntity].futureValue

    val request = Post("/msg").addCredentials(bearerTokenTestUser).withEntity(chatMessageEntity)
    chatMessageId = request ~> routes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      entityAs[ChatMessage].id should not be chatMessage.id
      entityAs[ChatMessage].id
    }
  }

  "be able to update geoEventId in ChatMessage (Put /msg/{id})" in {
    val chatMessage = ChatMessage(
      id = chatMessageId,
      userId = testUser.id,
      eventId = "any",
      message = "my message updated",
      timestamp = new Timestamp(System.currentTimeMillis())
    )
    val chatMessageEntity = Marshal(chatMessage).to[MessageEntity].futureValue
    val request = Put(uri = "/msg").addCredentials(bearerTokenTestUser).withEntity(chatMessageEntity)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[SuccessResponse].successMsg should be("Successfully updated ChatMessage")
    }
  }

  "be able to get all ChatMessage for specific user (GET /ChatMessages)" in {
    val eventId = "any"
    val request = Get(uri = s"/msg?eventId=$eventId").addCredentials(bearerTokenTestUser)
    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      val ChatMessages = entityAs[Seq[ChatMessage]]
      ChatMessages should not be empty
      ChatMessages.foreach { ChatMessage =>
        ChatMessage.userId should be(testUser.id)
      }
    }
  }

  "be able to delete a ChatMessage (DELETE /ChatMessages/{id})" in {
    val request = Delete(uri = s"/msg/$chatMessageId").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[SuccessResponse].successMsg should be("Successfully deleted ChatMessage")
    }
  }
}
