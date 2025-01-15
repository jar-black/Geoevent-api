package com.geoevent.registies

import cats.effect.unsafe.implicits.global
import com.geoevent.models.GeoStampModel._
import doobie.implicits._

import doobie.implicits.javasql._

class GeoStampRegistry extends RegistryCalls[GeoStamp] {
  override def _create(geoStamp: GeoStamp): GeoStamp = {
    sql"""INSERT INTO geo_stamps (id, user_id, latitude, longitude, timestamp)
         VALUES (${geoStamp.id},${geoStamp.userId},${geoStamp.latitude},${geoStamp.longitude},CURRENT_TIMESTAMP)"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => geoStamp
      case _ => throw new Exception("Error creating a geoStamp")
    }
  }

  override def _delete(id: String): Int = {
    sql"DELETE FROM geo_stamps WHERE id = $id"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _get(id: String): Option[GeoStamp] = {
    sql"""SELECT * FROM geo_stamps WHERE id = $id"""
      .query[GeoStamp]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  def getAllGeoStamps(userId: String): Seq[GeoStamp] = {
    sql"""SELECT * FROM geo_stamps where user_id = $userId"""
      .query[GeoStamp]
      .to[List]
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _update(geoStamp: GeoStamp): Int = {
    println("Not implemented")
    1
  }
}
