package info.lindblad.prometheus.cloudwatch.proxy.model

import info.lindblad.prometheus.cloudwatch.proxy.util.Logging
import info.lindblad.prometheus.cloudwatch.proxy.model.Count
import spray.http.FormData

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object MessageParser extends Logging {

  import ExecutionContext.Implicits.global

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
    groupByMetricIndex(data).flatMap(
      p => {
        val fields = p._2
        if (fields.contains("Value")) {
          for {
            name <- fields.get("MetricName")
            value <- Try(fields.getOrElse("Value", "0").toDouble).toOption
            unit <- fields.get("Unit")
          } yield Count("NeedToSetThis", name, value, unit)
        } else {
          for {
            name <- fields.get("MetricName")
            count <- Try(fields.getOrElse("StatisticValues.SampleCount", "0").toDouble).toOption
            sum <- Try(fields.getOrElse("StatisticValues.Sum", "0").toDouble).toOption
            min <- Try(fields.getOrElse("StatisticValues.Minimum", "0").toDouble).toOption
            max <- Try(fields.getOrElse("StatisticValues.Maximum", "0").toDouble).toOption
            unit <- fields.get("Unit")
          } yield StatisticsSet("NeedToSetThis", name, count, sum, min, max, unit)
        }
      }
    ).toSeq
  }

  def parseFuture(data: Seq[(String, String)]): Future[Seq[Metric]] = Future(parse(data))

}
