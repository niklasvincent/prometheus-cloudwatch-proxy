package info.lindblad.prometheus.cloudwatch.proxy.directives

import java.io.StringWriter
import java.util

import akka.actor.Status.Failure
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import info.lindblad.prometheus.cloudwatch.proxy.actor.ExposePrometheusData
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import io.prometheus.client.Collector.MetricFamilySamples
import io.prometheus.client.exporter.common.TextFormat
import spray.routing._

import scala.concurrent.duration._
import scala.util.Success

class PrometheusDirective(metricsActor: ActorRef) extends Directives with Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5.seconds)

  def retrievePrometheusData = metricsActor ? ExposePrometheusData

  def convertDataToString(data: util.Enumeration[MetricFamilySamples]): String = {
    val writer = new StringWriter()
    TextFormat.write004(writer, data)
    val dataAsString = writer.toString
    writer.close()
    dataAsString
  }

  val route: Route =
    path("metrics") {
      get {
        logger.debug("Prometheus data export requested")
        onSuccess(retrievePrometheusData) { data =>
          complete(convertDataToString(data.asInstanceOf[util.Enumeration[MetricFamilySamples]]))
        }
      }
    }
}