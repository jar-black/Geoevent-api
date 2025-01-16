package com.geoevent.models

import spray.json.DefaultJsonProtocol.{FloatJsonFormat, IntJsonFormat, StringJsonFormat, jsonFormat1, jsonFormat9, seqFormat}
import spray.json.RootJsonFormat
import java.sql.Timestamp

object GeoEventModel extends JsonImplicitFormats {
  case class GeoEvent(
                       id: String,
                       userId: String,
                       latitude: Float,
                       longitude: Float,
                       description: String,
                       radiusMeter: Int,
                       timeBeforeMinutes: Int,
                       timeAfterMinutes: Int,
                       timestamp: Timestamp
                     )

  case class GeoEvents(geoEvents: Seq[GeoEvent])

  implicit val geoEventJsonFormat: RootJsonFormat[GeoEvent] = jsonFormat9(GeoEvent)
  implicit val geoEventsJsonFormat: RootJsonFormat[GeoEvents] = jsonFormat1(GeoEvents)
}
