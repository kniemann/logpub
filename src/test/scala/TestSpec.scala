/**
  * Created by kevin on 3/27/17.
  */
import org.scalatest._

class TestSpec extends FlatSpec with Matchers {
  val pattern = """\[(.*?)\] (DEBUG|INFO|WARN|ERROR) (.*?) \((.*?)\)""".r

  val startDate = """^\[(.*?)\]""".r.unanchored

  "Regex" should "match date value in Log4j" in {
    val statement = "[2017-03-27 17:47:39,297] INFO [Kafka Server 0], shutting down (kafka.server.KafkaServer)"
    statement match {
      case pattern(date,level,text,logclass) => println(s"Date = $date Level = $level Text = $text Logclass = $logclass")
      case _ => println("Nothing")
    }
    statement match {
      case startDate(date) => println(s"Date = $date")
      case _ => println("Nothing again")
    }
  }

  it should "have a second test" in {
  }
}
