name := """logpub"""

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "Apache Snapshot Repository" at "https://repository.apache.org/snapshots"

libraryDependencies ++= {

  val akkaVersion = "2.4.17"
  val reactiveKafkaVersion = "0.14"
  val kafkaVersion = "0.10.2.0"
  val playVersion = "2.6.0-M6"
  val scalatestVersion = "3.0.1"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-stream-kafka" % reactiveKafkaVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.apache.kafka" % "kafka_2.12" % kafkaVersion,
    "com.typesafe.play" % "play-json_2.12" % playVersion,
    "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0-SNAPSHOT"
  )
}

fork in run := true
