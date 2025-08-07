/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ngrnotify.infrastructure

import uk.gov.hmrc.ngrnotify.config.AppConfig

import java.time.{Duration, ZonedDateTime}
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.*
import scala.language.postfixOps

trait Schedule {
  def timeUntilNextRun(): FiniteDuration
}

trait RegularSchedule extends Schedule {
  def timeUntilNextRun(): FiniteDuration
}

@Singleton
class DefaultRegularSchedule @Inject() (ngrConfig: AppConfig) extends RegularSchedule {
  override def timeUntilNextRun(): FiniteDuration = ngrConfig.exportFrequency seconds
}

class DefaultDailySchedule @Inject() (ngrConfig: AppConfig, clock: Clock) extends RegularSchedule {

  private val importScheduleHour   = ngrConfig.importScheduleHour
  private val importScheduleMinute = ngrConfig.importScheduleMinute

  def timeUntilNextRun(): FiniteDuration = {
    val now         = clock.now()
    val todayRun    = today(now, importScheduleHour, importScheduleMinute)
    val tomorrowRun = tomorrow(now, importScheduleHour, importScheduleMinute)
    val target      = if now.plusMinutes(1).isBefore(todayRun) then todayRun else tomorrowRun
    Duration.between(now, target).toMinutes minutes
  }

  private def tomorrow(now: ZonedDateTime, hour: Int, minute: Int): ZonedDateTime =
    now.plusDays(1).withHour(hour).withMinute(minute)

  private def today(now: ZonedDateTime, hour: Int, minute: Int): ZonedDateTime =
    now.withHour(hour).withMinute(minute)

}
