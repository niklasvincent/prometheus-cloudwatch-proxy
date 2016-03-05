package info.lindblad.prometheus.cloudwatch.proxy.actor

import java.util

import akka.actor.Actor
import info.lindblad.prometheus.cloudwatch.proxy.model.{MetricStore, Metric, StatisticsSet, Count}
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import io.prometheus.client.Collector.MetricFamilySamples

case object ExposePrometheusData

class MetricsActor extends Actor with Logging {

  val metricStore = new MetricStore()

  def receive = {
    case metrics: Seq[Metric] => {
      metrics.foreach {
        case count: Count => {
          logger.debug(s"Incrementing counter ${count.name} by ${count.value}")
          metricStore.add(count)
        }
        case statisticsSet: StatisticsSet => {
          logger.debug(s"Got statistics set ${statisticsSet.name}")
          metricStore.add(statisticsSet)
        }
      }
    }
    case ExposePrometheusData => {
      logger.debug("Asked to expose Prometheus data")
      val exposedPrometheusData: util.Enumeration[MetricFamilySamples] = metricStore.expose()
      sender ! exposedPrometheusData
    }
    case _ => logger.debug(s"Unsupported message received")
  }
}