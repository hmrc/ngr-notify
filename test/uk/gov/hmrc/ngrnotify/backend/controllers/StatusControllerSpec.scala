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
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import play.api.{Application, inject}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordControllerSpec
import uk.gov.hmrc.ngrnotify.backend.controllers.actions.FakeIdentifierAuthAction
import uk.gov.hmrc.ngrnotify.controllers.StatusController
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction

import java.io.IOException

class StatusControllerSpec extends AnyWordControllerSpec:

  private val controller = inject[StatusController]

  given Materializer = NoMaterializer

  override def fakeApplication(): Application =
    val httpClientV2Mock = mock[HttpClientV2]
    // DO NOT instruct the mock here, rather instruct it for each of the test cases below.
    new GuiceApplicationBuilder()
      .overrides(bind[HttpClientV2].to(httpClientV2Mock),
        bind[IdentifierAction].to[FakeIdentifierAuthAction])
      .build()

  "StatusController" should {

    ".getRatepayerStatus return 200" in {
      val client = inject[HttpClientV2]
      client
        .whenGetting("/job/ratepayers/GGID123345/dashboard")
        .thenReturn(rightResponseWith(OK, Some("ratepayerGetStatus.json")))
      val identifierRequest = FakeRequest().withHeaders("X-Cred-Id" -> "GGID123345")
      val result = controller.getRatepayerStatus(identifierRequest)
      status(result)          shouldBe OK
      contentAsString(result) shouldBe """{"activeRatepayerPersonExists":false,"activeRatepayerPersonaExists":false,"activePropertyLinkCount":0}"""
    }

    ".getRatepayerStatus return 500 for wrong ID" in {
      val client = inject[HttpClientV2]
      client
        .whenGetting("/job/ratepayers/1234567891255/dashboard")
        .thenReturn(rightResponseWith(BAD_REQUEST, Some("ratepayerWrongID.json")))

      val result = controller.getRatepayerStatus(FakeRequest().withHeaders("X-Cred-Id" -> "1234567891255"))
      status(result)        shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) should include("Invalid format for Id")
    }

    ".getRatepayerStatus return 500 if HIP response body is not a JSON" in {
      val client = inject[HttpClientV2]
      client
        .whenGetting("/job/ratepayers/test_no_json/dashboard")
        .thenReturn(rightResponseWith(OK))

      val result = controller.getRatepayerStatus(FakeRequest().withHeaders("X-Cred-Id" -> "test_no_json"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(
        result
      )              shouldBe """[{"code":"ACTION_FAILED","reason":"No content to map due to end-of-input\n at [Source: (String)\"\"; line: 1, column: 0]"}]"""
    }

    ".getRatepayerStatus return 500 on HIP connection exception" in {
      val client = inject[HttpClientV2]
      client
        .whenGetting("/job/ratepayers/test_hip_connection_error/dashboard")
        .thenReturn(leftResponseWith(IOException("HIP connection error details")))

      val result = controller.getRatepayerStatus(FakeRequest().withHeaders("X-Cred-Id" -> "test_hip_connection_error"))
      status(result)          shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe """[{"code":"ACTION_FAILED","reason":"HIP connection error details"}]"""
    }

  }
