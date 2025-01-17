package com.geoevent.routes

import com.geoevent.Authorization
import com.geoevent.models.ChatMessageModel.ChatMessage
import com.geoevent.models.ResponseModels.{ErrorResponse, SuccessResponse}
import com.geoevent.registies.ChatMessageRegistry
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.enrichAny

import java.util.UUID

object ChatMessageRoutes extends ChatMessageRegistry {
  val chatMessageRoutes: Route = {
    Authorization.authorizeToken { userId =>
      pathPrefix("msg") {
        pathEnd {
          post {
            entity(as[ChatMessage]) { chatMessage =>
              complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(chatMessage.copy(id = UUID.randomUUID.toString)).toJson.compactPrint))
            }
          } ~
            get {
              parameters("eventId".as[String]) { eventId =>
                complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, getAllChatMessages(eventId, userId).toJson.compactPrint))
              }
            } ~
            put {
              entity(as[ChatMessage]) { chatMessage =>
                _update(chatMessage) match {
                  case 1 => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, SuccessResponse("Successfully updated ChatMessage").toJson.compactPrint))
                  case _ => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ErrorResponse("ChatMessage not found").toJson.compactPrint))
                }
              }
            }
        } ~
          path(Segment) { id =>
            delete {
              _delete(id, userId) match {
                case 1 => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, SuccessResponse("Successfully deleted ChatMessage").toJson.compactPrint))
                case _ => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ErrorResponse("ChatMessage not found").toJson.compactPrint))
              }
            }
          }
      }
    }
  }
}
