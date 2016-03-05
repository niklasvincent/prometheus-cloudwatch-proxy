package info.lindblad.prometheus.cloudwatch.proxy.model

import java.util

import info.lindblad.prometheus.cloudwatch.proxy.model.Count
import info.lindblad.prometheus.cloudwatch.proxy.util.Hash
import io.prometheus.client.Collector.MetricFamilySamples

import scala.collection.mutable

import io.prometheus.client.{Summary, Counter, CollectorRegistry}

trait Metrics {

  def add(count: Count)

  def add(statisticsSet: StatisticsSet)

}

class MetricStore extends Metrics {

  lazy val prometheusRegistry = new CollectorRegistry()

  private lazy val counters = new mutable.HashMap[String, Counter]

  private lazy val summaries = new mutable.HashMap[String, Summary]

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
  }

  def expose(): util.Enumeration[MetricFamilySamples] = prometheusRegistry.metricFamilySamples()
}
