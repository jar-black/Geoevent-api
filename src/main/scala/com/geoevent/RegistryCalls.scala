package com.geoevent

import com.geoevent.database.DbConnection
import doobie.ConnectionIO
import org.apache.pekko.http.scaladsl.server.Route

import java.util.UUID
import scala.concurrent.Future

  trait RegistryCalls[A] extends DbConnection {
    //def _getAll: Future[Seq[A]]
    def _create(user: A): A
    def _delete(id: String): Int
    def _get(id: String): Option[A]
    def _update(user: A): Int
}
