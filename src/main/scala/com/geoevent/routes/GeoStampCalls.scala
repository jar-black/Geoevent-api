package com.geoevent.routes

import com.geoevent.Authorization
import com.geoevent.models.GeoStampModel._
import com.geoevent.models.ResponseModels.{ErrorResponse, SuccessResponse}
import com.geoevent.registies.GeoStampRegistry
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.enrichAny

import java.util.UUID


object GeoStampCalls extends GeoStampRegistry {
  val geoStampRoutes: Route =
    Authorization.authorizeToken { userId =>
      pathPrefix("geostamps") {
        pathEnd {
          get {
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, getAllGeoStamps(userId).toJson.compactPrint))
          } ~
            post {
              entity(as[GeoStamp]) { geostamp =>
                complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(geostamp.copy(id = UUID.randomUUID.toString, userId = userId)).toJson.compactPrint))
              }
            }
        } ~
          path(Segment) { id =>
            get {
              complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, _get(id).map(_.toJson.compactPrint)
                .getOrElse(ErrorResponse("GeoStamp not found").toJson.compactPrint)))
            } ~
              delete {
                _delete(id, userId) match {
                  case 1 => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, SuccessResponse("Successfully deleted GeoStamp").toJson.compactPrint))
                  case _ => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ErrorResponse("GeoStamp not found").toJson.compactPrint))
                }
              } ~
              put {
                entity(as[GeoStamp]) { geostamp =>
                  updateEventId(geostamp.id, geostamp.geoEventId) match {
                    case 1 => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, SuccessResponse("Successfully updated GeoStamp").toJson.compactPrint))
                    case _ => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ErrorResponse("GeoStamp not found").toJson.compactPrint))
                  }
                }
              }
          }
      }
    }
}
