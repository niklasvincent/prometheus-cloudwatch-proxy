package info.lindblad.prometheus.cloudwatch.proxy.model

import scala.collection.mutable


class MetricStore {

  private lazy val counters = new mutable.HashMap[String, Double]().withDefaultValue(0)

  def incrementCounter(name: String, increment: Double) = counters.update(name, counters(name) + increment)

}
