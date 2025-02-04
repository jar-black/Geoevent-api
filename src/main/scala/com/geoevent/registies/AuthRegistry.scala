package com.geoevent.registies

import cats.effect.unsafe.implicits.global
import com.geoevent.database.DbConnection
import com.geoevent.models.AuthModel.AuthModel
import doobie.implicits.javasql._
import doobie.implicits._

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

class AuthRegistry extends DbConnection {
  def getValidToken(userId: String): Option[AuthModel] = {
    val maybeToken = sql"""SELECT token FROM auth WHERE user_id = $userId AND valid_timestamp > CURRENT_TIMESTAMP"""
      .query[String]
      .option
      .transact(transactor)
      .unsafeRunSync()
    maybeToken.flatMap(token => {
      val timestamp: Timestamp = Timestamp.from(Instant.now().plusSeconds(3600 * 24)) // Last 24 hours
      sql"""UPDATE auth SET valid_timestamp = $timestamp WHERE token = $token"""
        .update
        .run
        .transact(transactor)
        .unsafeRunSync() match {
        case 1 => Some(AuthModel(token, userId, timestamp.toString))
        case _ => createAuthToken(userId)
      }
    }).orElse(createAuthToken(userId))
  }

  def getUserIdFromToken(token: String): Option[String] = {
    sql"""SELECT user_id FROM auth where token = $token"""
      .query[String]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  private def createAuthToken(userId: String): Option[AuthModel] = {
    val token:String = UUID.randomUUID().toString
    val timestamp: Timestamp = Timestamp.from(Instant.now().plusSeconds(3600 * 24)) // Last 24 hours
    sql"""INSERT INTO auth (token, user_id, valid_timestamp) VALUES ($token, $userId, $timestamp)"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => Some(AuthModel(token, userId, timestamp.toString))
      case _ => None
    }
  }
}
