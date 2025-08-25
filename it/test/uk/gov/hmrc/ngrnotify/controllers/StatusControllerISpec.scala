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

package uk.gov.hmrc.ngrnotify.controllers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.ngrnotify.model.response.RatepayerStatusResponse

class StatusControllerISpec extends AnyWordSpec with Matchers {
  val app: Application             = new GuiceApplicationBuilder().build()
  val controller: StatusController = app.injector.instanceOf[StatusController]

  "StatusController GET /ratepayerStatus/:id" should {
    "return 200 OK and ACCEPTED for TEST_ACCEPTED" in {
      val request = FakeRequest(GET, "/ratepayerStatus/TEST_ACCEPTED")
      val result  = controller.ratepayerStatus("TEST_ACCEPTED")(request)

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "ratepayerStatus").as[String] shouldBe "ACCEPTED"
      (json \ "error").as[String]           shouldBe "Registered. The ratepayer details have been accepted by the VOA."
    }

    "return 200 OK and UNKNOWN for unknown id" in {
      val request = FakeRequest(GET, "/ratepayerStatus/foobar")
      val result  = controller.ratepayerStatus("not a valid id")(request)

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "ratepayerStatus").as[String] shouldBe "UNKNOWN"
    }

    "return valid JSON for all known test IDs" in {
      val testIds = Seq("TEST_UNKNOWN", "TEST_INPROGRESS", "TEST_ACCEPTED", "TEST_REJECTED")

      testIds.foreach { id =>
        val request = FakeRequest(GET, s"/ratepayerStatus/$id")
        val result  = controller.ratepayerStatus(id)(request)

        status(result) shouldBe OK
        val json = contentAsJson(result)
        json.validate[RatepayerStatusResponse].isSuccess shouldBe true
      }
    }
  }
}
