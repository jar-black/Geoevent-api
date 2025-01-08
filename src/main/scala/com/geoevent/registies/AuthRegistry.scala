package com.geoevent.registies

import cats.effect.unsafe.implicits.global
import com.geoevent.database.DbConnection
import doobie.implicits._

import java.sql.{Date, Timestamp}
import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.UUID

class AuthRegistry extends DbConnection {
  def getAuthToken(id: String): Option[String] = {
    sql"""SELECT token FROM authorization id = $id"""
      .query[String]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  def createAuthToken(id: String): Option[String] = {
    val token = UUID.randomUUID().toString
    val timestamp = Timestamp.from(Instant.now().plusSeconds(3600*24)) // Last 24 hours
    sql""" INSERT INTO authorization (token, user_id, valid_timestamp) VALUES (${token}, ${id}, ${timestamp})"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => Some(token)
      case _ => None
    }
  }
}
