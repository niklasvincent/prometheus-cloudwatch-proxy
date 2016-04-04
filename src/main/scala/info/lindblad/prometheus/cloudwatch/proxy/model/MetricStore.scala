package info.lindblad.prometheus.cloudwatch.proxy.model

import java.util

import info.lindblad.prometheus.cloudwatch.proxy.util.Hash
import io.prometheus.client.Collector.MetricFamilySamples

import scala.collection.mutable

import io.prometheus.client.{Histogram, Counter, CollectorRegistry}

trait Metrics {

  def add(count: Count)

  def add(statisticsSet: StatisticsSet)

}

class MetricStore extends Metrics {

  lazy val prometheusRegistry = new CollectorRegistry()

  private lazy val counters = new mutable.HashMap[String, Counter]

  private lazy val histograms = new mutable.HashMap[String, Histogram]

  def add(count: Count) = {
    val hash = Hash.sha1(count.toString)
    val name = s"${count.namespace}:${count.name}"
    counters.getOrElseUpdate(
      hash,
      Counter.build()
        .name(name)
        .help(name)
        .labelNames(count.dimensions.map(_.name): _*)
        .register(prometheusRegistry)
    ).labels(count.dimensions.map(_.value): _*).inc(count.value)
  }

  def add(statisticsSet: StatisticsSet) = {
    val hash = Hash.sha1(statisticsSet.toString)
    val name = s"${statisticsSet.namespace}:${statisticsSet.name}"
    if (statisticsSet.sampleCount > 0) {
      val average: Double = statisticsSet.sum / statisticsSet.sampleCount
      histograms.getOrElseUpdate(
        hash,
        Histogram.build()
          .name(name)
          .help(name)
          .labelNames(statisticsSet.dimensions.map(_.name): _*)
          .register(prometheusRegistry)
      ).labels(statisticsSet.dimensions.map(_.value): _*).observe(average)
    }
  }

  def expose(): util.Enumeration[MetricFamilySamples] = prometheusRegistry.metricFamilySamples()
}
