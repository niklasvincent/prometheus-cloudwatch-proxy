package info.lindblad.prometheus.cloudwatch.proxy.model

import scala.collection.mutable

import io.prometheus.client.{Summary, Counter, CollectorRegistry}


class MetricStore {

  lazy val prometheusRegistry = new CollectorRegistry()

  private lazy val counters = new mutable.HashMap[String, Counter]

  private lazy val summaries = new mutable.HashMap[String, Summary]

  Counter.build().name("requests_total").help("Total requests.").register(prometheusRegistry)


}
