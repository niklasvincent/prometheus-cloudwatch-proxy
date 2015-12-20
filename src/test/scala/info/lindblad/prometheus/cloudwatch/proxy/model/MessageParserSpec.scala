package info.lindblad.prometheus.cloudwatch.proxy.model

import org.scalatest.{FlatSpec, Matchers}

class MessageParserSpec extends FlatSpec with Matchers {

  val r = scala.util.Random

  def randomString = (r.alphanumeric take r.nextInt(20) + 1).mkString("")

  val alwaysPresentFields = Seq(
    ("Action", "PutMetricData"),
    ("Version", "2010-08-01")
  )

  def metricFields(index: Integer, name: String, value: String, unit: String): Seq[(String, String)] = Seq(
    (s"MetricData.member.${index}.MetricName", name),
    (s"MetricData.member.${index}.Value", value),
    (s"MetricData.member.${index}.Unit", unit)
  )

  def statisticsSetFields(index: Integer, name: String, sampleCount: String, sum: String, minimum: String, maximum: String, unit: String): Seq[(String, String)] = Seq(
    (s"MetricData.member.${index}.MetricName", name),
    (s"MetricData.member.${index}.StatisticValues.SampleCount", sampleCount),
    (s"MetricData.member.${index}.StatisticValues.Sum", sum),
    (s"MetricData.member.${index}.StatisticValues.Minimum", minimum),
    (s"MetricData.member.${index}.StatisticValues.Maximum", maximum),
    (s"MetricData.member.${index}.Unit", unit)
  )

  def metric(index: Integer = 1) = metricFields(
    index,
    randomString,
    r.nextDouble().toString,
    randomString
  )

  def statistics(index: Integer = 1) = statisticsSetFields(
    index,
    randomString,
    r.nextDouble().toString,
    r.nextDouble().toString,
    r.nextDouble().toString,
    r.nextDouble().toString,
    randomString
  )

  "MessageParser" should "parse the maximum number of metrics (20)" in {
    val data = alwaysPresentFields ++ (1 to 20).flatMap(metric(_)).toSeq

    val metrics = MessageParser.parse(data)

    metrics.length should be(20)
  }

  "MessageParser" should "parse single metric correctly" in {
    val data = alwaysPresentFields ++ Seq(
      ("Namespace", "MyNamespace"),
      ("MetricData.member.1.MetricName", "Exceptions"),
      ("MetricData.member.1.Value", "100"),
      ("MetricData.member.1.Unit", "Count")
    )

    val metrics = MessageParser.parse(data)

    metrics.length should be(1)
    metrics(0).isInstanceOf[Count] should be(true)
    val count = metrics(0).asInstanceOf[Count]
    count.name should be("Exceptions")
    count.value should be(100.0)
    count.unit should be("Count")
  }

  "MessageParser" should "parse single statistics set correctly" in {
    val data = alwaysPresentFields ++ Seq(
      ("Namespace", "MyNamespace"),
      ("MetricData.member.3.MetricName", "Latency"),
      ("MetricData.member.3.StatisticValues.SampleCount", "20.0"),
      ("MetricData.member.3.StatisticValues.Sum", "700.0"),
      ("MetricData.member.3.StatisticValues.Minimum", "10.0"),
      ("MetricData.member.3.StatisticValues.Maximum", "1000.0"),
      ("MetricData.member.3.Unit", "milliseconds")
    )

    val metrics = MessageParser.parse(data)

    metrics.length should be(1)
    metrics(0).isInstanceOf[StatisticsSet] should be(true)
    val statisticsSet = metrics(0).asInstanceOf[StatisticsSet]
    statisticsSet.name should be("Latency")
    statisticsSet.sampleCount should be(20.0)
    statisticsSet.sum should be(700.0)
    statisticsSet.minimum should be(10.0)
    statisticsSet.maximum should be(1000.0)
    statisticsSet.unit should be("milliseconds")
  }

  "MessageParser" should "not parse metric without name" in {
    val data = alwaysPresentFields ++ Seq(
      ("MetricData.member.1.Value", "100"),
      ("MetricData.member.1.Unit", "Count")
    )

    val metrics = MessageParser.parse(data)

    metrics.length should be(0)
  }



}
