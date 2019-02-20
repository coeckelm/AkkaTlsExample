package com.ing.api.routes

import akka.event.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.ing.api.application.AppContext
import io.circe.generic.extras.{Configuration => CirceConfiguration}

import scala.concurrent.duration._

trait AppRoutes extends AppContext{


  implicit private val genDevConfig: CirceConfiguration =
    CirceConfiguration.default.withDiscriminator("what_am_i")

  lazy val log = Logging(system, this.getClass)
  implicit lazy val timeout: Timeout = Timeout(15.seconds)


  lazy val commonRoutes: Route =
      path("helloworld") {
        get {
          complete(HttpResponse(StatusCodes.OK, entity = "Hello World"))
        }
      }


  lazy val httpRoutes: Route = pathPrefix("rokcet") {
    concat(
      path("hellofreeworld") {
        get {
          complete(HttpResponse(StatusCodes.OK, entity = "Hellow Free World"))
        }
      },
      commonRoutes
    )
  }

  lazy val httpsRoutes: Route = pathPrefix("rokcet") {
    concat(
      path("helloworld" / Segment) {username =>
        get {
          complete(HttpResponse(StatusCodes.OK, entity = s"Hello World and hello to $username too"))
        }
      },
      commonRoutes
    )
  }


}
