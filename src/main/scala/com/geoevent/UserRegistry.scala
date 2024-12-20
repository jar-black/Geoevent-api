package com.geoevent

import cats.effect.unsafe.implicits.global
import doobie.implicits._

import java.util.UUID
import scala.concurrent.Future
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.effect.IO
import org.apache.pekko.http.scaladsl.server.Route


trait UserRegistry extends RegistryCalls[User] {
  def userRoutes: Route
/*
  override def _getAll: Future[Seq[User]] = {
    sql"select * from users".query[User]
      .to[List]
      .transact(transactor)
      .unsafeToFuture()
  }
*/

  override def _create(user: User): User = {
    sql"""INSERT INTO users (id, name, phone, validated) VALUES (${user.id},${user.name},${user.phone},${user.validated})"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => user
      case _ => throw new Exception("Error creating user")
    }
  }

  override def _delete(id: String): Int = {
    sql"DELETE FROM users WHERE id = $id"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _get(id: String): Option[User] = {
    sql"SELECT * FROM users WHERE id = $id"
      .query[User]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _update(user: User): Int = {
    sql"""UPDATE users SET name = ${user.name}, phone = ${user.phone}, validated = ${user.validated} WHERE id = ${user.id}"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }
}
