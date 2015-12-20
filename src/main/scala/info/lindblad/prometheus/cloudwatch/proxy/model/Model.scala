package info.lindblad.prometheus.cloudwatch.proxy.model

sealed trait Metric

case class Count(namespace: String, name: String, value: Double, unit: String = "Count") extends Metric

case class StatisticsSet(namespace: String, name: String, sampleCount: Double, sum: Double, minimum: Double, maximum: Double, unit: String) extends Metric