package info.lindblad.prometheus.cloudwatch.proxy.actor

import akka.actor.ActorRef
import info.lindblad.prometheus.cloudwatch.proxy.directives.{PrometheusDirective, CloudWatchDirective}
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import spray.http.{HttpRequest, HttpResponse, Timedout}
import spray.routing.directives.LoggingMagnet
import spray.routing.{HttpServiceActor, Route}

class HttpRequestActor(metricsActor: ActorRef) extends HttpServiceActor with Logging {

  override def actorRefFactory = context

  def requestMethodAndResponseStatusAsInfo(req: HttpRequest): Any => Unit = {
    case res: HttpResponse => logger.debug(s"${req}")
    case _ =>
  }

  def handleTimeouts: Receive = {
    case Timedout(request: HttpRequest) => logger.info("{} timed out", request.uri.path)
  }

  def routeWithLogging(routing: Route) = logRequestResponse(LoggingMagnet(requestMethodAndResponseStatusAsInfo _))(routing)

  override def receive: Receive = runRoute(routeWithLogging(route))

  val route = new CloudWatchDirective(metricsActor).route ~ new PrometheusDirective(metricsActor).route

}