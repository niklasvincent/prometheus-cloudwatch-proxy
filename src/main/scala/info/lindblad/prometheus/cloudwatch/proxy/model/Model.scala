package info.lindblad.prometheus.cloudwatch.proxy.model

case class Dimension(name: String, value: String)

sealed trait Metric

case class Count(namespace: String, name: String, value: Double, unit: String = "Count", dimensions: Seq[Dimension] = Seq.empty) extends Metric

case class StatisticsSet(namespace: String, name: String, sampleCount: Double, sum: Double, minimum: Double, maximum: Double, unit: String, dimensions: Seq[Dimension] = Seq.empty) extends Metric