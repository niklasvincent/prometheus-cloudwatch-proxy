package info.lindblad.prometheus.cloudwatch.proxy.model

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}

class MessageParserSpec extends PropSpec with PropertyChecks with Matchers {

  implicit override val generatorDrivenConfig = PropertyCheckConfig(minSuccessful = 1000)

  val validChars = Gen.listOf(Gen.choose(35.toChar, 126.toChar)).map(_.mkString).suchThat(_.length > 0)
  val doubleValues = Arbitrary.arbitrary[Double]
  val statisticsSet = Gen.nonEmptyListOf(doubleValues)
  val statisticsSets = Gen.nonEmptyListOf(statisticsSet)
  val nbrOfMetrics = Gen.choose(1, 20)

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

  property("Parse metrics") {
    forAll(validChars, doubleValues, validChars, nbrOfMetrics) { (name: String, value: Double, unit: String, size: Int) =>
      val data = alwaysPresentFields ++ (1 to size).flatMap(metricFields(_, name, value.toString, unit)).toSeq
      val metrics = MessageParser.parse(data)
      metrics.length should be(size)
    }
  }

  property("Parse statistics sets") {
    forAll(validChars, statisticsSets, validChars, nbrOfMetrics) { (name: String, measurements: Seq[Seq[Double]], unit: String, size: Int) =>
      measurements.foreach(measurement => {
        val sampleCount = measurement.size.toString
        val sum = measurement.sum.toString
        val minimum = measurement.min.toString
        val maximum = measurement.max.toString
        val data = alwaysPresentFields ++ (1 to size).flatMap(statisticsSetFields(_, name, sampleCount, sum, minimum, maximum, unit)).toSeq
        val metrics = MessageParser.parse(data)
        metrics.length should be(size)
      })
    }
  }

  property("Parse single metric") {
    forAll(validChars, doubleValues, validChars) { (name: String, value: Double, unit: String) =>
      val data = alwaysPresentFields ++ metricFields(1, name, value.toString, unit)
      val metrics = MessageParser.parse(data)
      metrics.length should be(1)
      metrics(0).isInstanceOf[Count] should be(true)
      val count = metrics(0).asInstanceOf[Count]
      count.name should be(name)
      count.value should be(value)
      count.unit should be(unit)
    }
  }

  property("Parse single statistics set") {
    forAll(validChars, statisticsSet, validChars) { (name: String, measurement: Seq[Double], unit: String) =>
      val sampleCount = measurement.size
      val sum = measurement.sum
      val minimum = measurement.min
      val maximum = measurement.max
      val data = alwaysPresentFields ++ statisticsSetFields(
        1,
        name,
        sampleCount.toString,
        sum.toString,
        minimum.toString,
        maximum.toString,
        unit
      )
      val metrics = MessageParser.parse(data)
      metrics.length should be(1)
      metrics(0).isInstanceOf[StatisticsSet] should be(true)
      val statisticsSet = metrics(0).asInstanceOf[StatisticsSet]
      statisticsSet.name should be(name)
      statisticsSet.sampleCount should be(sampleCount)
      statisticsSet.sum should be(sum)
      statisticsSet.minimum should be(minimum)
      statisticsSet.maximum should be(maximum)
      statisticsSet.unit should be(unit)
    }
  }

}
