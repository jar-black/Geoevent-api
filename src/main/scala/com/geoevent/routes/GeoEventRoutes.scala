package com.geoevent.routes

import com.geoevent.Authorization
import com.geoevent.models.GeoEventModel.GeoEvent
import com.geoevent.models.ResponseModels.{ErrorResponse, SuccessResponse}
import com.geoevent.registies.GeoEventRegistry
import com.geoevent.routes.GeoStampCalls._delete
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.enrichAny

import java.util.UUID

object GeoEventRoutes extends GeoEventRegistry {
  val geoEventRoutes: Route =
    Authorization.authorizeToken { userId =>
      pathPrefix("geoevents") {
        pathEnd {
          get {
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, getAllGeoEvents(userId).toJson.compactPrint))
          } ~
            post {
              entity(as[GeoEvent]) { geoEvent =>
                println("geoEvent")
                complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, _create(geoEvent.copy(id = UUID.randomUUID.toString, userId = userId)).toJson.compactPrint))
              }
            }
        } ~
          path(Segment) { id =>
            get {
              complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, _get(id).map(_.toJson.compactPrint)
                .getOrElse(ErrorResponse("GeoEvent not found").toJson.compactPrint)))
            } ~
              delete {
                _delete(id, userId) match {
                  case 1 => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, SuccessResponse("Successfully deleted GeoEvent").toJson.compactPrint))
                  case _ => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ErrorResponse("GeoEvent not found").toJson.compactPrint))
                }
              }
          }
      }
    }
}
