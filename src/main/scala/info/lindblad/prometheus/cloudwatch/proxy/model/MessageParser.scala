package info.lindblad.prometheus.cloudwatch.proxy.model

import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import info.lindblad.prometheus.cloudwatch.proxy.model.Count
import spray.http.FormData

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object MessageParser extends Logging {

  def groupByMetricIndex(data: Seq[(String, String)]): Map[String, Map[String, String]] = {
    data.filter(
      p => p._1.startsWith("MetricData.member.")
    ).groupBy(
        q => q._1.substring(18, q._1.indexOf(".", 18))
    ).map(
        r => r._1 -> r._2.map(s => s._1.substring(s._1.indexOf(".", 18)+1) -> s._2).toMap
    )
  }

  def parse(data: Seq[(String, String)]): Seq[Metric] = {
    val namespace = data.filter(
      p => p._1.equals("Namespace")
    ).map(
      q => q._2
    ).headOption.getOrElse("Default")

    groupByMetricIndex(data).flatMap(
      p => {
        val fields = p._2
        fields.contains("Value") match {
          case true => parseCount(namespace, fields)
          case false => parseStatisticsSet(namespace, fields)
        }
      }
    ).toSeq
  }

  def parseCount(namespace: String, fields: Map[String, String]): Option[Count] = {
    for {
      name <- fields.get("MetricName")
      value <- Try(fields.getOrElse("Value", "0").toDouble).toOption
      unit <- fields.get("Unit")
    } yield Count(namespace, name, value, unit)
  }

  def parseStatisticsSet(namespace: String, fields: Map[String, String]): Option[StatisticsSet] = {
    for {
      name <- fields.get("MetricName")
      count <- Try(fields.getOrElse("StatisticValues.SampleCount", "0").toDouble).toOption
      sum <- Try(fields.getOrElse("StatisticValues.Sum", "0").toDouble).toOption
      min <- Try(fields.getOrElse("StatisticValues.Minimum", "0").toDouble).toOption
      max <- Try(fields.getOrElse("StatisticValues.Maximum", "0").toDouble).toOption
      unit <- fields.get("Unit")
    } yield StatisticsSet(namespace, name, count, sum, min, max, unit)
  }

}
