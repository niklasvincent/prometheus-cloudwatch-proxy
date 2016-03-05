package info.lindblad.prometheus.cloudwatch.proxy.directives

import akka.actor.ActorRef
import info.lindblad.prometheus.cloudwatch.proxy.model.CloudWatchMessageParser
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import spray.routing._

class CloudWatchDirective(metricsActor: ActorRef) extends Directives with Logging {

  def uuid = java.util.UUID.randomUUID.toString

  val route: Route =
    path("cloudwatch" /) {
      post {
        entity(as[spray.http.FormData]) { data =>
          val metrics = CloudWatchMessageParser.parse(data.fields)
          metricsActor ! metrics
          complete {
            <PutMetricDataResponse>
              <ResponseMetadata>
                <RequestId>uuid</RequestId>
              </ResponseMetadata>
            </PutMetricDataResponse>
          }
        }
      }
    }
}