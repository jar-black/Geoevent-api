package com.geoevent.registies

import com.geoevent.models.ChatMessageModel._
import doobie.implicits.toSqlInterpolator
import cats.effect.unsafe.implicits.global
import doobie.implicits.javasql._
import doobie.implicits._

class ChatMessageRegistry extends RegistryCalls[ChatMessage] {

  override def _create(item: ChatMessage): ChatMessage = {
    sql"""INSERT INTO chat_messages (id, user_id, event_id, message, timestamp) VALUES (${item.id},${item.userId}, ${item.eventId},${item.message},CURRENT_TIMESTAMP)"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => item
      case _ => throw new Exception("Error creating a chat message")
    }
  }

  def _delete(id: String, userId: String): Int = {
    sql"DELETE FROM chat_messages WHERE id = $id AND user_id = $userId"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _get(id: String): Option[ChatMessage] = {
    sql"""SELECT * FROM chat_messages WHERE id = $id"""
      .query[ChatMessage]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _update(item: ChatMessage): Int = {
    sql"""UPDATE chat_messages SET message = ${item.message} WHERE id = ${item.id}"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  def getAllChatMessages(eventId: String, userId: String): Seq[ChatMessage] = {
    sql"""SELECT * FROM chat_messages where event_id = $eventId AND user_id = $userId"""
      .query[ChatMessage]
      .to[List]
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _delete(id: String): Int = ???
}
