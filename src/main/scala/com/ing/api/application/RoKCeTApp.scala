package com.ing.api.application

import akka.Done
import akka.http.scaladsl.server.Route
import com.ing.api.routes.AppRoutes
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Promise



object RoKCeTApp extends App with Node with AppRoutes with LazyLogging{

  def promise = Promise[Done]
  start()

  def start() = {
    system.log.info(s"Starting RoKCeT application")
    //system.actorOf(Props[RoKCeTActor], name = "rokcetactor")
    startup()
  }


  /**
    * Override to implement the routes that will be served by this http server.
    */
  override protected def routes: Route = httpRoutes


}
