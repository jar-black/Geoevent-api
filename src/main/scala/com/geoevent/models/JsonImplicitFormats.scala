package com.geoevent.models

import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.{JsValue, RootJsonFormat}

import java.sql.Timestamp

trait JsonImplicitFormats {
  implicit val timestampJsonFormat: RootJsonFormat[Timestamp] = new RootJsonFormat[Timestamp] {
    override def read(json: JsValue): Timestamp = Timestamp.valueOf(StringJsonFormat.read(json))
    override def write(obj: Timestamp): JsValue = StringJsonFormat.write(obj.toString)
  }
}
