package com.geoevent.encrypt

import com.github.t3hnar.bcrypt._

import scala.util.Try

object Encrypt {
  def hashPassword(password: String): Option[String] = {
    val salt = generateSalt
    password.bcryptSafeBounded(salt).toOption
  }
  def validateHash(password: String, hash: String): Boolean = {
    password.isBcryptedSafeBounded(hash).toOption.getOrElse(false)
  }
}
