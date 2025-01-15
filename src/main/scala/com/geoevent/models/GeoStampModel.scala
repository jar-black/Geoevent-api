package com.geoevent.models

import spray.json.DefaultJsonProtocol.{FloatJsonFormat, StringJsonFormat, jsonFormat1, jsonFormat5, seqFormat}
import spray.json.{JsValue, RootJsonFormat}
import java.sql.Timestamp

object GeoStampModel {
  case class GeoStamp(id: String, userId: String, latitude: Float, longitude: Float, timestamp: Timestamp)
  case class GeoStamps(geoStamps: Seq[GeoStamp])

  implicit val timestampJsonFormat: RootJsonFormat[Timestamp] = new RootJsonFormat[Timestamp] {
    override def read(json: JsValue): Timestamp = Timestamp.valueOf(StringJsonFormat.read(json))
    override def write(obj: Timestamp): JsValue = StringJsonFormat.write(obj.toString)
  }
  implicit val geoStampJsonFormat: RootJsonFormat[GeoStamp] = jsonFormat5(GeoStamp)
  implicit val geoStampsJsonFormat: RootJsonFormat[GeoStamps] = jsonFormat1(GeoStamps)

}
