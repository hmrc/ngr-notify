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

package uk.gov.hmrc.ngrnotify

import org.apache.pekko.actor.ActorSystem
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import uk.gov.hmrc.ngrnotify.config.{AppConfig, NGRAudit}
import uk.gov.hmrc.ngrnotify.connectors.{CallbackConnector, EmailConnector}
import uk.gov.hmrc.ngrnotify.exporter.*
import uk.gov.hmrc.ngrnotify.infrastructure.RegularSchedule
import uk.gov.hmrc.ngrnotify.repository.EmailNotificationRepo

import java.time.Clock
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class NGRImpl @Inject() (
  actorSystem: ActorSystem,
  appConfig: AppConfig,
  audit: NGRAudit,
  systemClock: Clock,
  emailConnector: EmailConnector,
  callbackConnector: CallbackConnector,
  regularSchedule: RegularSchedule,
  implicit val ec: ExecutionContext,
  mongoLockRepository: MongoLockRepository,
  emailNotificationRepo: EmailNotificationRepo
) {

  import appConfig.*

  if submissionExportEnabled then
    val exporter = new ExportEmailNotificationVOA(
      emailNotificationRepo,
      systemClock,
      audit,
      emailConnector,
      callbackConnector,
      appConfig
    )
    new SendEmailExporter(
      mongoLockRepository,
      exporter,
      exportBatchSize,
      actorSystem.scheduler,
      actorSystem.eventStream,
      regularSchedule
    ).start()
}
