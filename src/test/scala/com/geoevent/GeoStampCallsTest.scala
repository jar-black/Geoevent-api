package com.geoevent

import com.geoevent.models.GeoStampModel._
import com.geoevent.models.ResponseModels.SuccessResponse
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import spray.json.DefaultJsonProtocol.seqFormat

import java.sql.Timestamp

class GeoStampCallsTest extends TestFrame {
  var geoStampId: String = _

  "be able to add geostamp (POST /geostamps)" in {
    val geostamp = GeoStamp(
      id = "any",
      userId = testUser.id,
      latitude = 1.0f,
      longitude = 1.0f,
      timestamp = new Timestamp(System.currentTimeMillis()),
      None
    )
    val geostampEntity = Marshal(geostamp).to[MessageEntity].futureValue

    val request = Post("/geostamps").addCredentials(bearerTokenTestUser).withEntity(geostampEntity)
    geoStampId = request ~> routes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      entityAs[GeoStamp].id should not be geostamp.id
      entityAs[GeoStamp].id
    }
  }

  "be able to update geoEventId in geostamp (Put /geostamps/{id})" in {
    val geostamp = GeoStamp(
      id = geoStampId,
      userId = testUser.id,
      latitude = 1.0f,
      longitude = 1.0f,
      timestamp = new Timestamp(System.currentTimeMillis()),
      Some("EventId")
    )
    val geostampEntity = Marshal(geostamp).to[MessageEntity].futureValue
    val request = Put(uri = s"/geostamps/$geoStampId").addCredentials(bearerTokenTestUser).withEntity(geostampEntity)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[SuccessResponse].successMsg should be("Successfully updated GeoStamp")
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

  "be able to delete a geostamp (DELETE /geostamps/{id})" in {
    val request = Delete(uri = s"/geostamps/$geoStampId").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[SuccessResponse].successMsg should be("Successfully deleted GeoStamp")
    }
  }
}
