package info.lindblad.prometheus.cloudwatch.proxy.directives

import info.lindblad.prometheus.cloudwatch.proxy.Main
import info.lindblad.prometheus.cloudwatch.proxy.model.MessageParser
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import spray.routing._

object CloudWatchDirective extends Directives with Logging {

  val route: Route =
    path("cloudwatch" /) {
      post {
        entity(as[spray.http.FormData]) { data =>
          val metrics = MessageParser.parse(data.fields)
          println(data.fields)
          println(metrics)
          complete {
            <PutMetricDataResponse xmlns="http://127.0.0.1:8080/doc/2010-08-01/">
              <ResponseMetadata>
                <RequestId>e16fc4d3-9a04-11e0-9362-093a1cae5385</RequestId>
              </ResponseMetadata>
            </PutMetricDataResponse>
          }
        }
      }
    }
}