package com.geoevent.registies

import com.geoevent.database.DbConnection

  trait RegistryCalls[A] extends DbConnection {
    //def _getAll: Future[Seq[A]]
    def _create(item: A): A
    def _delete(id: String): Int
    def _get(id: String): Option[A]
    def _update(item: A): Int
}
