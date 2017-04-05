package org.kan.logpub

import java.io.File
import java.net.InetAddress
import java.nio.file.Paths
import akka.Done
import org.apache.kafka.clients.producer.ProducerRecord
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.LogManager
import play.api.libs.json._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by kevin on 3/26/17.
  */

object LogPub {
  val logger = LogManager.getLogger("LogPub")
  def main(args: Array[String]): Unit = {
    val LogPub = new LogPub
    val publish = LogPub.publish

    publish.onComplete {
      case Success(_) => logger.info("future completed successfully")
      case Failure(e) => logger.error(s"future failed $e", e)
    }
  }
}
class LogPub() {
  val logger = LogManager.getLogger("LogPub")
  def publish: Future[Done] = {
    // actor system and implicit materializer
    implicit val system = ActorSystem("Sys")
    implicit val materializer = ActorMaterializer()

    val pattern = """\[(.*?)\] (DEBUG|INFO|WARN|ERROR) (.*?) \(([^\s]+)\)$""".r


    val conf = ConfigFactory.load
    val logFile = new File(conf.getString("logpub.path"))
    val path = logFile.getAbsolutePath
    logger.info(s"Reading from $path")
    val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")
      .withCloseTimeout(Duration(1000,MILLISECONDS))

    FileIO.fromPath(Paths.get(conf.getString("logpub.path"))).
      via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 65536, allowTruncation = true))
      .map[String](_.utf8String)
      .map {
        case pattern(date, level, text, logClass) =>
          JsObject(Seq(
            "app" -> JsString("Kafka"),
            "datetime" -> JsString(date),
            "level" -> JsString(level),
            "class" -> JsString(logClass),
            "host" -> JsString(InetAddress.getLocalHost.toString),
            "text" -> JsString(text)
          ))
        case line @ other =>
          JsObject(Seq(
            "app" -> JsString("Kafka"),
            "datetime" -> JsNull,
            "level" -> JsNull,
            "class" -> JsNull,
            "host" -> JsString(InetAddress.getLocalHost.toString),
            "text" -> JsString(line)
          ))
      }.map { elem =>
      new ProducerRecord[String, String]("output",(elem.asInstanceOf[JsValue] \ "app").get.as[String], Json.prettyPrint(elem))
    }.runWith(Producer.plainSink(producerSettings))
  }
}
