package com.ing.api.application

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

trait AppContext {



  implicit val system: ActorSystem = ActorSystem("akkatlsexample")
  println(s"actorSystem ${system.name} started")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
}
