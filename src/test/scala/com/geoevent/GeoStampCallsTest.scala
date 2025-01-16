package com.geoevent

import com.geoevent.models.GeoEventModel.GeoEvent
import com.geoevent.models.GeoStampModel._
import com.geoevent.models.ResponseModels.SuccessResponse
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import spray.json.DefaultJsonProtocol.seqFormat

import java.sql.Timestamp
import java.util.UUID

class GeoStampCallsTest extends TestFrame {
  var geoStampId:String = _

  "be able to add geostamp (POST /geostamps)" in {
    val geostamp = GeoStamp(
      id = "any",
      userId = testUser.id,
      latitude = 1.0f,
      longitude = 1.0f,
      timestamp = new Timestamp(System.currentTimeMillis())
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

  "be able to delete a geostamp (DELETE /geostamps)" in {
    val request = Delete(uri = s"/geostamps/$geoStampId").addCredentials(bearerTokenTestUser)

    request ~> routes ~> check {
      status should be(StatusCodes.OK)
      contentType should be(ContentTypes.`application/json`)
      entityAs[SuccessResponse].successMsg should be("Successfully deleted GeoStamp")
    }
  }

}
