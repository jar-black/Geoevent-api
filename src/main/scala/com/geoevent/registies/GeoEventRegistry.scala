package com.geoevent.registies

import cats.effect.unsafe.implicits.global
import com.geoevent.models.GeoEventModel.GeoEvent
import doobie.implicits.toSqlInterpolator
import doobie.implicits._
import doobie.implicits.javasql._

import java.util.UUID

class GeoEventRegistry extends RegistryCalls[GeoEvent] {

  override def _create(item: GeoEvent): GeoEvent = {
    sql"""INSERT INTO geo_events (id, user_id, latitude, longitude, description, radius_meter, time_before_minutes, time_after_minutes, timestamp)
         VALUES (${item.id},${item.userId},${item.latitude},${item.longitude},${item.description},${item.radiusMeter},${item.timeBeforeMinutes},${item.timeAfterMinutes},CURRENT_TIMESTAMP)"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync() match {
      case 1 => item
      case _ => throw new Exception("Error creating a geoEvent")
    }
  }

  def _delete(id: String, userId:String): Int = {
    sql"DELETE FROM geo_events WHERE id = $id AND user_id = $userId"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _get(id: String): Option[GeoEvent] = {
    sql"""SELECT * FROM geo_events WHERE id = $id"""
      .query[GeoEvent]
      .option
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _update(item: GeoEvent): Int = ???

  def getAllGeoEvents(userId: String): Seq[GeoEvent] = {
    println("Getting all geo events")
    sql"""SELECT * FROM geo_events where user_id = $userId"""
      .query[GeoEvent]
      .to[List]
      .transact(transactor)
      .unsafeRunSync()
  }

  override def _delete(id: String): Int = ???
}
