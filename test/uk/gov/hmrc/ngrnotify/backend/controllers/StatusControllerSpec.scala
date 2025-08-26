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

package uk.gov.hmrc.ngrnotify.backend.controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.ngrnotify.controllers.StatusController
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus.INPROGRESS

class StatusControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {
  val controllerComponents: ControllerComponents = Helpers.stubControllerComponents()
  val controller                                 = new StatusController(controllerComponents)

  "ratepayerStatus()" should {
    "return 200 OK with valid JSON" in {
      val request = FakeRequest(GET, "/ratepayer/status/12345")
      val result  = controller.ratepayerStatus("TEST_INPROGRESS")(request)

      status(result) shouldBe OK

      val json = contentAsJson(result)
      (json \ "ratepayerStatus").as[RatepayerStatus] shouldBe INPROGRESS
      (json \ "error").asOpt[String]                 shouldBe Some(
        "In progress. Case officers are examining the ratepayer application but have not yet decided."
      )
    }
  }
}
