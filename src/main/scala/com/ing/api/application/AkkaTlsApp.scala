package com.ing.api.application

import akka.actor.Props
import akka.http.scaladsl.server.Route
import com.ing.api.actor.SampleActor
import com.ing.api.routes.AppRoutes
import com.typesafe.scalalogging.LazyLogging



object AkkaTlsApp extends App with AppContext with Node with AppRoutes with LazyLogging{


  system.log.info(s"Starting AkkaTlsExample application")
  system.actorOf(Props[SampleActor], name = "sampleactor")

  println(s"actorSystem ${system.name} started")

  startup()


  /**
    * Override to implement the routes that will be served by this http server.
    */
  override protected def routes: Route = httpRoutes


}
