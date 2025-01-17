package com.geoevent.models

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat1, jsonFormat5, seqFormat}
import spray.json.RootJsonFormat

import java.sql.Timestamp

object ChatMessageModel extends JsonImplicitFormats {
  case class ChatMessage(
                          id: String,
                          userId: String,
                          eventId: String,
                          message: String,
                          timestamp: Timestamp
                        )

  case class ChatMessages(chatMessages: Seq[ChatMessage])

  implicit val geoEventJsonFormat: RootJsonFormat[ChatMessage] = jsonFormat5(ChatMessage)
  implicit val geoEventsJsonFormat: RootJsonFormat[ChatMessages] = jsonFormat1(ChatMessages)
}
