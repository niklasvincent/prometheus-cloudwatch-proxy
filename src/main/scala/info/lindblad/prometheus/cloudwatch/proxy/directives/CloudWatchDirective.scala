package info.lindblad.prometheus.cloudwatch.proxy.directives

import info.lindblad.prometheus.cloudwatch.proxy.Main
import info.lindblad.prometheus.cloudwatch.proxy.model.MessageParser
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import spray.routing._

object CloudWatchDirective extends Directives with Logging {

  def uuid = java.util.UUID.randomUUID.toString

  val route: Route =
    path("cloudwatch" /) {
      post {
        entity(as[spray.http.FormData]) { data =>
          val metrics = MessageParser.parse(data.fields)
          println(data.fields)
          println(metrics)
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