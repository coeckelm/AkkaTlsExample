package com.ing.api.routes

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import io.circe.generic.extras.{Configuration => CirceConfiguration}

import scala.concurrent.duration._

trait AppRoutes {

  implicit private val genDevConfig: CirceConfiguration =
    CirceConfiguration.default.withDiscriminator("what_am_i")

  implicit lazy val timeout: Timeout = Timeout(15.seconds)


  lazy val httpRoutes: Route = pathPrefix("tls") {
    concat(
      path("helloworld") {
        get {
          complete(HttpResponse(StatusCodes.OK, entity = "Hello World"))
        }
      },
      path("hellofreeworld") {
        get {
          complete(HttpResponse(StatusCodes.OK, entity = "Hellow Free World"))
        }
      },
      path("helloworld" / Segment) {username =>
        get {
          complete(HttpResponse(StatusCodes.OK, entity = s"Hello World and hello to $username too"))
        }
      }
    )
  }



}
