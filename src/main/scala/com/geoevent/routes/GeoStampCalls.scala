package com.geoevent.routes

import com.geoevent.Authorization
import com.geoevent.models.GeoStampModel._
import com.geoevent.registies.GeoStampRegistry
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.enrichAny


object GeoStampCalls extends GeoStampRegistry {
  val geoStampsRoutes: Route =
    Authorization.authorizeToken { userId =>
      pathPrefix("geostamps") {
        pathEnd {
          get {
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, getAllGeoStamps(userId).toJson.compactPrint))
          }
        } ~
          pathEnd {
            post {
              entity(as[GeoStamp]) { geostamp =>
                complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(geostamp.copy(userId = userId)).toJson.compactPrint))
              }
            }
          }
      }
    }
}
