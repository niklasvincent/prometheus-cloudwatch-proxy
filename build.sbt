import com.typesafe.sbt.SbtNativePackager

name := "prometheus-cloudwatch-proxy"

version := "0.1"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV   % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "org.specs2" %%  "specs2-core" % "2.4.17" % "test",
    "org.scalatest" %%  "scalatest" % "2.2.1" % "test",
    "org.scalacheck" %%  "scalacheck" % "1.12.5" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.2"
  )
}

mainClass in Compile := Some("info.lindblad.prometheus.cloudwatch.proxy.Main")

lazy val root = (project in file(".")).enablePlugins(SbtNativePackager).enablePlugins(JavaServerAppPackaging)
