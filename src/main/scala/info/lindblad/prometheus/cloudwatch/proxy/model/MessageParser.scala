package info.lindblad.prometheus.cloudwatch.proxy.model

import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import info.lindblad.prometheus.cloudwatch.proxy.model.Count
import spray.http.FormData

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object MessageParser extends Logging {

  val metricDataMemberPrefix = "MetricData.member."
  lazy val metricDataMemberOffset = metricDataMemberPrefix.length

  def groupByMetricIndex(data: Seq[(String, String)]): Map[String, Map[String, String]] = {
    data.filter(_._1.startsWith(metricDataMemberPrefix)
    ).groupBy(
        q => q._1.substring(metricDataMemberOffset, q._1.indexOf(".", metricDataMemberOffset))
    ).map(
        r => r._1 -> r._2.map(s => s._1.substring(s._1.indexOf(".", metricDataMemberOffset)+1) -> s._2).toMap
    )
  }

  def parse(data: Seq[(String, String)]): Seq[Metric] = {
    val namespace = extractNameSpace(data)
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

  def extractNameSpace(data: Seq[(String, String)]): String = data.filter(_._1.equals("Namespace")).map(_._2).headOption.getOrElse("Default")

  def parseDouble(string: Option[String]): Option[Double] = Try(string.getOrElse("0").toDouble).toOption

  def parseCount(namespace: String, fields: Map[String, String]): Option[Count] = {
    for {
      name <- fields.get("MetricName")
      value <- parseDouble(fields.get("Value"))
      unit <- fields.get("Unit")
    } yield Count(namespace, name, value, unit)
  }

  def parseStatisticsSet(namespace: String, fields: Map[String, String]): Option[StatisticsSet] = {
    for {
      name <- fields.get("MetricName")
      count <- parseDouble(fields.get("StatisticValues.SampleCount"))
      sum <- parseDouble(fields.get("StatisticValues.Sum"))
      min <- parseDouble(fields.get("StatisticValues.Minimum"))
      max <- parseDouble(fields.get("StatisticValues.Maximum"))
      unit <- fields.get("Unit")
    } yield StatisticsSet(namespace, name, count, sum, min, max, unit)
  }

}
