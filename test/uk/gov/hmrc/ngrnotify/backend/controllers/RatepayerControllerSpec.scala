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

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.testkit.NoMaterializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers, Injecting}
import uk.gov.hmrc.ngrnotify.controllers.RatepayerController
import uk.gov.hmrc.ngrnotify.model.Address
import uk.gov.hmrc.ngrnotify.model.ratepayer.AgentStatus.agent
import uk.gov.hmrc.ngrnotify.model.ratepayer.RatepayerType.organization
import uk.gov.hmrc.ngrnotify.model.ratepayer.RegisterRatepayerRequest

class RatepayerControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with Injecting:

  private val controller = inject[RatepayerController]

  given Materializer = NoMaterializer

  "RatepayerController" should {
    "return 202" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withJsonBody(Json.toJson(
          RegisterRatepayerRequest(
            "login",
            Some(organization),
            Some(agent),
            "Full name",
            None,
            "test@email.com",
            Some("QQ123456A"),
            "1111",
            None,
            Address("Line 1", Some("Line 2"), "City", None, "ZZ11 1ZZ")
          )
        ))

      val result = controller.registerRatepayer.apply(fakeRequest)
      status(result)          shouldBe ACCEPTED
      contentAsString(result) shouldBe """{"status":"OK"}"""
    }

    "return 400" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")

      val result = controller.registerRatepayer(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }
  }
