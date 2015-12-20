package info.lindblad.prometheus.cloudwatch.proxy.actor

import akka.actor.Actor

class MetricsActor extends Actor {
  def receive = {
    case "hello" => println("hello back at you")
    case _ => println("hello?")
  }
}