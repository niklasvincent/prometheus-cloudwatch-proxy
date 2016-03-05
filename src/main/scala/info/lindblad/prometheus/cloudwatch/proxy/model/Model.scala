package info.lindblad.prometheus.cloudwatch.proxy.model

case class Dimension(name: String, value: String) {
  override def toString: String = s"$name=$value"
}

sealed trait Metric

case class Count(namespace: String, name: String, value: Double, unit: String = "Count", dimensions: Seq[Dimension] = Seq.empty) extends Metric {
  override def toString: String = s"$namespace,$name,$unit,${dimensions.toString()}"
}

case class StatisticsSet(namespace: String, name: String, sampleCount: Double, sum: Double, minimum: Double, maximum: Double, unit: String, dimensions: Seq[Dimension] = Seq.empty) extends Metric {
  override def toString: String = s"$namespace,$name,$unit,${dimensions.toString()}"
}