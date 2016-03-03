package info.lindblad.prometheus.cloudwatch.proxy.actor

import akka.actor.Actor
import info.lindblad.prometheus.cloudwatch.proxy.model.{MetricStore, Metric, StatisticsSet, Count}
import info.lindblad.prometheus.cloudwatch.proxy.util.Logging

class MetricsActor extends Actor with Logging {

  val metricStore = new MetricStore()

  def receive = {
    case metrics: Seq[Metric] => {
      metrics.foreach(m =>
        m match {
          case count: Count => {
            logger.debug(s"Incrementing counter ${count.name} by ${count.value}")
          }
          case statisticsSet: StatisticsSet => logger.debug("Got statistics set")
        }
      )
    }
    case _ => println("hello?")
  }
}