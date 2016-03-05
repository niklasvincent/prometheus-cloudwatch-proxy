package info.lindblad.prometheus.cloudwatch.proxy

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import info.lindblad.prometheus.cloudwatch.proxy.actor.{MetricsActor, HttpRequestActor}
import info.lindblad.prometheus.cloudwatch.proxy.conf.Configuration
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {

  val metricsSystem = ActorSystem("metrics-sysytem")

  implicit val httpRequestSystem = ActorSystem("http-request-system")

  val metricsActor: ActorRef = metricsSystem.actorOf(Props[MetricsActor], name = "MetricsActor")

  val serviceActor: ActorRef = httpRequestSystem.actorOf(Props(new HttpRequestActor(metricsActor)))

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(serviceActor, interface = "0.0.0.0", port = Configuration.port)
}