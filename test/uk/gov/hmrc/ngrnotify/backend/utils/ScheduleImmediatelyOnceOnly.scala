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

package uk.gov.hmrc.ngrnotify.backend.utils

import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.infrastructure.{DefaultDailySchedule, DefaultRegularSchedule, SystemClock}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.*
import scala.language.postfixOps

@Singleton
class ScheduleImmediatelyOnceOnly @Inject() (ngrConfig: AppConfig, systemClock: SystemClock)
    extends DefaultDailySchedule(ngrConfig, systemClock) {
  private var hasRun = false

  override def timeUntilNextRun(): FiniteDuration =
    if hasRun then 100 days
    else
      hasRun = true
      0.5 seconds
}

@Singleton
class ScheduleEverySecond @Inject() (ngrConfig: AppConfig) extends DefaultRegularSchedule(ngrConfig) {
  var times                                       = 1
  override def timeUntilNextRun(): FiniteDuration = 1 second
}
