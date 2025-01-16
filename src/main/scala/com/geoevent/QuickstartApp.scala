package com.geoevent

import com.geoevent.database.DbConnection
import com.geoevent.routes.{AuthCalls, GeoEventRoutes, GeoStampCalls, UserCalls}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route

import scala.util.{Failure, Success}

//#main-class
object QuickstartApp {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    DbConnection.flywayMigration()

    val rootBehavior = Behaviors.setup[Nothing] { context =>
      startHttpServer(
        UserCalls.userRoutes ~
          AuthCalls.authorizationRoutes ~
          GeoStampCalls.geoStampRoutes ~
          GeoEventRoutes.geoEventRoutes
      )(context.system)
      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "GeoEventActorSystem")
  }
}
