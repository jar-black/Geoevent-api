package com.geoevent.models

import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.{JsValue, RootJsonFormat}

import java.sql.Timestamp

trait JsonImplicitFormats {
  implicit val timestampJsonFormat: RootJsonFormat[Timestamp] = new RootJsonFormat[Timestamp] {
    override def read(json: JsValue): Timestamp = Timestamp.valueOf(StringJsonFormat.read(json))
    override def write(obj: Timestamp): JsValue = StringJsonFormat.write(obj.toString)
  }
  implicit val optionJsonFormat: RootJsonFormat[Option[String]] = new RootJsonFormat[Option[String]] {
    override def read(json: JsValue): Option[String] = Some(StringJsonFormat.read(json))
    override def write(obj: Option[String]): JsValue = StringJsonFormat.write(obj.getOrElse(""))
  }
}
