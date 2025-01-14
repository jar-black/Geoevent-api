package com.geoevent.registies

import cats.effect.unsafe.implicits.global
import com.geoevent.encrypt.Encrypt
import com.geoevent.models.UserModel.User
import doobie.implicits._


trait UserRegistry extends RegistryCalls[User] {

  override def _create(item: User): User = {
    val passwordHash = Encrypt.hashPassword(item.passwordHash)
    sql"""INSERT INTO users (id, name, phone, password_hash, validated) VALUES (${item.id},${item.name},${item.phone},${passwordHash},${item.validated})"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => item
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

  override def _get(phone: String): Option[User] = {
    sql"SELECT * FROM users WHERE phone = $phone"
      .query[User]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  def getUserById(id: String): Option[User] = {
    sql"SELECT * FROM users WHERE id = $id"
      .query[User]
      .option
      .transact(transactor)
      .unsafeRunSync()
      .map(_.copy(passwordHash = ""))
  }

  override def _update(item: User): Int = {
    sql"""UPDATE users SET name = ${item.name}, phone = ${item.phone}, validated = ${item.validated} WHERE id = ${item.id}"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

//  override def _create(item: User): User = ???
//  override def _update(item: User): Int = ???
}
