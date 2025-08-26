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

package uk.gov.hmrc.ngrnotify.backend.services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus.*
import uk.gov.hmrc.ngrnotify.services.StatusService

class StatusServiceSpec extends AnyWordSpec with Matchers {

  "checkRatepayerStatus()" should {
    "return UNKNOWN for unrecognized id" in {
      StatusService.checkRatepayerStatus("random-id") shouldBe UNKNOWN
    }

    "return UNKNOWN for TEST_UNKNOWN" in {
      StatusService.checkRatepayerStatus("TEST_UNKNOWN") shouldBe UNKNOWN
    }

    "return INPROGRESS for TEST_INPROGRESS" in {
      StatusService.checkRatepayerStatus("TEST_INPROGRESS") shouldBe INPROGRESS
    }

    "return ACCEPTED for TEST_ACCEPTED" in {
      StatusService.checkRatepayerStatus("TEST_ACCEPTED") shouldBe ACCEPTED
    }

    "return REJECTED for TEST_REJECTED" in {
      StatusService.checkRatepayerStatus("TEST_REJECTED") shouldBe REJECTED
    }
  }

  "buildRatepayerStatusResponse()" should {
    "build response with correct status and error message for UNKNOWN" in {
      val response = StatusService.buildRatepayerStatusResponse(UNKNOWN)
      response.ratepayerStatus shouldBe UNKNOWN
      response.error           shouldBe Some(
        "Unknown. The bridge has no details of this ratepayer. Possibly a signal that something has gone wrong if the Ratepayer has registered via a frontend service."
      )
    }

    "build response with correct status and error message for INPROGRESS" in {
      val response = StatusService.buildRatepayerStatusResponse(INPROGRESS)
      response.ratepayerStatus shouldBe INPROGRESS
      response.error           shouldBe Some(
        "In progress. Case officers are examining the ratepayer application but have not yet decided."
      )
    }

    "build response with correct status and error message for ACCEPTED" in {
      val response = StatusService.buildRatepayerStatusResponse(ACCEPTED)
      response.ratepayerStatus shouldBe ACCEPTED
      response.error           shouldBe Some("Registered. The ratepayer details have been accepted by the VOA.")
    }

    "build response with correct status and error message for REJECTED" in {
      val response = StatusService.buildRatepayerStatusResponse(REJECTED)
      response.ratepayerStatus shouldBe REJECTED
      response.error           shouldBe Some("Rejected. The ratepayer details have been rejected by the VOA.")
    }
  }
}
