package com.geoevent.models

import spray.json.DefaultJsonProtocol.{FloatJsonFormat, StringJsonFormat, jsonFormat1, jsonFormat5, seqFormat}
import spray.json.RootJsonFormat
import java.sql.Timestamp

object GeoStampModel extends JsonImplicitFormats {
  case class GeoStamp(id: String, userId: String, latitude: Float, longitude: Float, timestamp: Timestamp)
  case class GeoStamps(geoStamps: Seq[GeoStamp])

  implicit val geoStampJsonFormat: RootJsonFormat[GeoStamp] = jsonFormat5(GeoStamp)
  implicit val geoStampsJsonFormat: RootJsonFormat[GeoStamps] = jsonFormat1(GeoStamps)
}
