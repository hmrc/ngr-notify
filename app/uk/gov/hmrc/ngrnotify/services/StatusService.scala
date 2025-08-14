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

package uk.gov.hmrc.ngrnotify.services

import uk.gov.hmrc.ngrnotify.model.RatepayerStatus
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus.*
import uk.gov.hmrc.ngrnotify.model.response.RatepayerStatusResponse

object StatusService {
  def checkRatepayerStatus(id: String): RatepayerStatus = id match {
    case "TEST_UNKNOWN" => UNKNOWN
    case "TEST_INPROGRESS" => INPROGRESS
    case "TEST_ACCEPTED" => ACCEPTED
    case "TEST_REJECTED" => REJECTED
    case _ => UNKNOWN
  }

  def buildRatepayerStatusResponse(ratepayerStatus: RatepayerStatus): RatepayerStatusResponse = ratepayerStatus match {
    case UNKNOWN => RatepayerStatusResponse(
      UNKNOWN,
      Some("Unknown. The bridge has no details of this ratepayer. Possibly a signal that something has gone wrong if the Ratepayer has registered via a frontend service."))
    case INPROGRESS => RatepayerStatusResponse(
      INPROGRESS,
      Some("In progress. Case officers are examining the ratepayer application but have not yet decided."))
    case ACCEPTED => RatepayerStatusResponse(
      ACCEPTED,
      Some("Registered. The ratepayer details have been accepted by the VOA."))
    case REJECTED => RatepayerStatusResponse(
      REJECTED,
      Some("Rejected. The ratepayer details have been rejected by the VOA."))
  }

  def buildRatepayerStatusResponseOLD(ratepayerStatus: RatepayerStatus): RatepayerStatusResponse = {
    RatepayerStatusResponse(ratepayerStatus, error = Some("PLACEHOLDER ERROR MESSAGE :)"))
  }
}
