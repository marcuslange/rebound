package io.pileworx.rebound

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.pileworx.rebound.application.{ReboundDao, ReboundService}
import io.pileworx.rebound.common.akka.AkkaImplicits
import io.pileworx.rebound.common.velocity.TemplateEngine
import io.pileworx.rebound.port.primary.rest.{MockRoutes, ReboundRoutes}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Application extends App with AkkaImplicits {

  val engine = new TemplateEngine
  val dao = new ReboundDao
  val service = new ReboundService(dao, engine)
  val routes: Route = new MockRoutes(service).routes ~ new ReboundRoutes(service).routes
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "0.0.0.0", 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println("Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}