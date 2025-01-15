package com.geoevent

import com.geoevent.models.GeoStampModel._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import spray.json.DefaultJsonProtocol.seqFormat

import java.sql.Timestamp
import java.util.UUID

class GeoStampCallsTest extends TestFrame {

  "be able to add geostamp (POST /geostamps)" in {
    val geostamp = GeoStamp(
      id = UUID.randomUUID().toString,
      userId = testUser.id,
      latitude = 1.0f,
      longitude = 1.0f,
      timestamp = new Timestamp(System.currentTimeMillis())
    )
    val geostampEntity = Marshal(geostamp).to[MessageEntity].futureValue

    val request = Post("/geostamps").addCredentials(bearerTokenTestUser).withEntity(geostampEntity)
    request ~> routes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      entityAs[GeoStamp].id should be(geostamp.id)
    }
  }

  "be able to get all geostamp for specific user (GET /geostamps)" in {
    val request = Get(uri = "/geostamps").addCredentials(bearerTokenTestUser)
    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      val geoStamps = entityAs[Seq[GeoStamp]]
      geoStamps should not be empty
      geoStamps.foreach { geoStamp =>
        geoStamp.userId should be(testUser.id)
      }
    }
  }
}
