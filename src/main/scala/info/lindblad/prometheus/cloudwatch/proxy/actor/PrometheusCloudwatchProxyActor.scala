package info.lindblad.prometheus.cloudwatch.proxy.actor

import info.lindblad.prometheus.cloudwatch.proxy.directives.CloudWatchDirective
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import spray.http.{HttpRequest, HttpResponse, Timedout}
import spray.routing.directives.LoggingMagnet
import spray.routing.{HttpServiceActor, Route}

class PrometheusCloudwatchProxyActor extends HttpServiceActor with Logging {

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

  val route = CloudWatchDirective.route

}