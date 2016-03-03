package info.lindblad.prometheus.cloudwatch.proxy.model

import info.lindblad.prometheus.cloudwatch.proxy.util.Logging

import scala.util.Try

object CloudWatchMessageParser extends Logging {

  val metricDataMemberPrefix = "MetricData.member."
  lazy val metricDataMemberOffset = metricDataMemberPrefix.length

  val metricDimensionMemberPrefix = "Dimensions.member."
  lazy val metricDimensionMemberOffset = metricDimensionMemberPrefix.length

  def groupBySubstring(data: Seq[(String, String)], substringLength: Int): Map[String, Map[String, String]] = {
    data.groupBy(
      q => q._1.substring(substringLength, q._1.indexOf(".", substringLength))
    ).map(
        r => r._1 -> r._2.map(s => s._1.substring(s._1.indexOf(".", substringLength) + 1) -> s._2).toMap
      )
  }

  def groupBySubstring(data: Map[String, String], substringLength: Int): Map[String, Map[String, String]] = {
    groupBySubstring(data.toIterable.toSeq, substringLength)
  }

  def groupByMetricIndex(data: Seq[(String, String)]): Map[String, Map[String, String]] = {
    groupBySubstring(data.filter(_._1.startsWith(metricDataMemberPrefix)), metricDataMemberOffset)
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

  def extractDimensions(fields: Map[String, String]): Seq[Dimension] = {
    groupBySubstring(fields.filterKeys(_.startsWith(metricDimensionMemberPrefix)), metricDimensionMemberOffset).flatMap(d => for {
      name <- d._2.get("Name")
      value <- d._2.get("Value")
    } yield Dimension(name, value)).toSeq
  }

  def parseDouble(string: Option[String]): Option[Double] = Try(string.getOrElse("0").toDouble).toOption

  def parseCount(namespace: String, fields: Map[String, String]): Option[Count] = {
    val dimensions = extractDimensions(fields)
    for {
      name <- fields.get("MetricName")
      value <- parseDouble(fields.get("Value"))
      unit <- fields.get("Unit")
    } yield Count(namespace, name, value, unit, dimensions)
  }

  def parseStatisticsSet(namespace: String, fields: Map[String, String]): Option[StatisticsSet] = {
    val dimensions = extractDimensions(fields)
    for {
      name <- fields.get("MetricName")
      count <- parseDouble(fields.get("StatisticValues.SampleCount"))
      sum <- parseDouble(fields.get("StatisticValues.Sum"))
      min <- parseDouble(fields.get("StatisticValues.Minimum"))
      max <- parseDouble(fields.get("StatisticValues.Maximum"))
      unit <- fields.get("Unit")
    } yield StatisticsSet(namespace, name, count, sum, min, max, unit, dimensions)
  }

}
