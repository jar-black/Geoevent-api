package com.geoevent

import com.geoevent.models.GeoEventModel._
import com.geoevent.models.ResponseModels.SuccessResponse
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import spray.json.DefaultJsonProtocol.seqFormat

import java.sql.Timestamp


class GeoEventRoutesTest extends TestFrame {
  var geoEventId:String = _

  "be able to add geoevents (POST /geoevents)" in {
    val geoEvent = GeoEvent(
      id = "any",
      userId = testUser.id,
      latitude = 1.0f,
      longitude = 1.0f,
      description = "test",
      radiusMeter = 1,
      timeBeforeMinutes = 1,
      timeAfterMinutes = 1,
      timestamp = new Timestamp(System.currentTimeMillis())
    )
    val geoEventEntity = Marshal(geoEvent).to[MessageEntity].futureValue

    val request = Post("/geoevents").addCredentials(bearerTokenTestUser).withEntity(geoEventEntity)
    geoEventId =  request ~> routes ~> check {
      status should be(StatusCodes.Created)
      contentType should be(ContentTypes.`application/json`)
      entityAs[GeoEvent].id should not be(geoEvent.id)
      entityAs[GeoEvent].id
    }
  }

  "be able to get all geoevents for specific user (GET /geoevents)" in {
    val request = Get("/geoevents").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      val geoStamps = entityAs[Seq[GeoEvent]]
      geoStamps should not be empty
      geoStamps.foreach { geoStamp =>
        geoStamp.userId should be(testUser.id)
      }
    }
  }

  "be able to delete a geoevent (DELETE /geoevents)" in {
    val request = Delete(uri = s"/geoevents/$geoEventId").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[SuccessResponse].successMsg should be("Successfully deleted GeoEvent")
    }
  }

}
