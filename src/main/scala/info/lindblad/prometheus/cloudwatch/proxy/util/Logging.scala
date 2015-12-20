package info.lindblad.prometheus.cloudwatch.proxy.util

import org.slf4j.LoggerFactory

trait Logging {
  val logger = LoggerFactory.getLogger(this.getClass)
}