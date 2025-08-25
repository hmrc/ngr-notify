/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrnotify.exporter

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestKit
import org.scalatest.flatspec.AsyncFlatSpecLike
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.mongo.lock.{Lock, MongoLockRepository}
import uk.gov.hmrc.ngrnotify.backend.base.MockitoExtendedSugar
import uk.gov.hmrc.ngrnotify.infrastructure.Schedule

import java.time.Instant.EPOCH
import scala.concurrent.Future
import scala.concurrent.Future.successful
import scala.concurrent.duration.*

class SendEmailExporterSpec
    extends TestKit(ActorSystem("test"))
    with AsyncFlatSpecLike
    with MockitoExtendedSugar
    with Matchers:

  it should "run a job" in {
    // SETUP
    val mongoLockRepository = mock[MongoLockRepository]
    when(mongoLockRepository.takeLock(any, any, any)).thenReturn(successful(Some(Lock("id", "owner", EPOCH, EPOCH))))

    val exportEmailNotification = mock[ExportEmailNotification]
    when(exportEmailNotification.exportNow(any)).thenReturn(successful(()))

    val schedule         = mock[Schedule]
    val timeUntilNextRun = 1000.millis
    when(schedule.timeUntilNextRun()).thenReturn(timeUntilNextRun)

    val exporterUnderTest = new SendEmailExporter(
      mongoLockRepository,
      exporter = exportEmailNotification,
      exportBatchSize = 100,
      system,
      schedule
    )

    // Register a subscriber to the Pekko EventStream
    // system.getEventStream.subscribe(testActor, classOf[SendEmailComplete])
    system.getEventStream.subscribe(testActor, classOf[Any])

    // EXERCISE (and sleep enough to ensure the job is run)
    exporterUnderTest.start()
    Thread.sleep(timeUntilNextRun.toMillis + 1000)

    // TODO VERIFY
    // within(timeUntilNextRun + 5.second) {
    //   val evt = expectMsgType[SendEmailComplete]
    //   evt.msg mustBe "SendEmailScheduler finished"
    //   verify(exportEmailNotification).exportNow(100)
    //   succeed
    // }
    succeed
  }
