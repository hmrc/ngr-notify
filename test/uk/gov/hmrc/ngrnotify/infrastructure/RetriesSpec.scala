package uk.gov.hmrc.ngrnotify.infrastructure

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.ActorSystem
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class RetriesSpec extends AnyWordSpec with Matchers with ScalaFutures {

  implicit val system: ActorSystem = ActorSystem("RetriesSpec")
  implicit val ec: ExecutionContext = system.dispatcher

  // Custom Retries implementation for testing
  object TestRetries extends Retries {
    override protected def actorSystem: ActorSystem = system
    override protected def configuration = None
  }

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Millis))

  "Retries" should {
    "return result if block succeeds first time" in {
      val result = TestRetries.retry("GET", "http://test") {
        Future.successful("ok")
      }
      whenReady(result) { _ shouldBe "ok" }
    }

    "retry the specified number of times on failure and then succeed" in {
      var attempts = 0
      val result = TestRetries.retry("GET", "http://test") {
        attempts += 1
        if (attempts < 3) Future.failed(new RuntimeException("fail"))
        else Future.successful("success")
      }
      whenReady(result) { _ shouldBe "success" }
      attempts shouldBe 3
    }

    "use custom intervals from config if provided" in {
      val config = ConfigFactory.parseString(
        """
          |http-verbs.retries.intervals = [100ms, 200ms]
          |""".stripMargin)
      val customRetries = new Retries {
        override protected def actorSystem: ActorSystem = system
        override protected def configuration = Some(config)
      }
      var attempts = 0
      val result = customRetries.retry("GET", "http://test") {
        attempts += 1
        if (attempts < 3) Future.failed(new RuntimeException("fail"))
        else Future.successful("done")
      }
      whenReady(result) { _ shouldBe "done" }
      attempts shouldBe 3
    }
  }
}
