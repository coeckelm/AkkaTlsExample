package com.ing.api.application

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

trait AppContext {

  val config: Config = ConfigFactory.load()

  var applicationName: String = config.getString("akka.application.name")

  implicit val system: ActorSystem = ActorSystem(config.getString("akka.actor-system.name"), config)
  println(s"actorSystem ${system.name} started")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
}
