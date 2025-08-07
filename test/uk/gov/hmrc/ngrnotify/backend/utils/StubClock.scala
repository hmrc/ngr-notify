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

package uk.gov.hmrc.ngrnotify.backend.utils

import play.api.Logging
import uk.gov.hmrc.ngrnotify.infrastructure.Clock
import uk.gov.hmrc.ngrnotify.utils.DateUtil.nowInUK

import java.time.ZonedDateTime
import javax.inject.Singleton

@Singleton
class StubClock extends Clock with Logging {

  private var _now: ZonedDateTime = nowInUK

  def setNow(d: ZonedDateTime): Unit = {
    logger.warn(s"Setting new time $d, old time ${_now}")
    _now = d
  }

  override def now(): ZonedDateTime = _now

}
