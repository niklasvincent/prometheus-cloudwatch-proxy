package info.lindblad.prometheus.cloudwatch.proxy

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import info.lindblad.prometheus.cloudwatch.proxy.actor.PrometheusCloudwatchProxyActor
import info.lindblad.prometheus.cloudwatch.proxy.conf.Configuration
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {

  implicit val system = ActorSystem("prometheus-cloudwatch-proxy")

  val service = system.actorOf(Props[PrometheusCloudwatchProxyActor])

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = Configuration.port)
}