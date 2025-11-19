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
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.RequestBuilderStub
import uk.gov.hmrc.ngrnotify.controllers.{RatepayerController, StatusController}
import uk.gov.hmrc.ngrnotify.model.email.Email
import uk.gov.hmrc.ngrnotify.model.ratepayer.AgentStatus.agent
import uk.gov.hmrc.ngrnotify.model.ratepayer.RatepayerType.organization
import uk.gov.hmrc.ngrnotify.model.ratepayer.*
import uk.gov.hmrc.ngrnotify.model.{Address, Postcode}

import java.io.IOException

class StatusControllerSpec extends AnyWordAppSpec:

  private val controller = inject[StatusController]

  given Materializer = NoMaterializer

  override def fakeApplication(): Application =
    val httpClientV2Mock = mock[HttpClientV2]

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/GGID123345/dashboard"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK), testResourceContent("ratepayerGetStatus.json")))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/1234567891255/dashboard"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(BAD_REQUEST), testResourceContent("ratepayerWrongID.json")))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/test_invalid_json/dashboard"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK), "{}"))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/test_no_json/dashboard"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK)))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/test_hip_connection_error/dashboard"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Left(IOException("HIP connection error details"))))

    new GuiceApplicationBuilder()
      .overrides(bind[HttpClientV2].to(httpClientV2Mock))
      .build()

  "StatusController" should {

    ".getRatepayerStatus return 200" in {
      val result = controller.getRatepayerStatus("GGID123345")(FakeRequest())
      status(result)          shouldBe OK
      contentAsString(result) shouldBe """{"activeRatepayerPersonExists":false,"activeRatepayerPersonaExists":false,"activePropertyLinkCount":0}"""
    }

    ".getRatepayerStatus return 500 for wrong ID" in {
      val result = controller.getRatepayerStatus("1234567891255")(FakeRequest())
      status(result)        shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) should include("Invalid format for Id")
    }

    ".getRatepayerStatus return 500 if HIP response body is not a JSON" in {
      val result = controller.getRatepayerStatus("test_no_json")(FakeRequest())
      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(
        result
      )              shouldBe """[{"code":"ACTION_FAILED","reason":"No content to map due to end-of-input\n at [Source: (String)\"\"; line: 1, column: 0]"}]"""
    }

    ".getRatepayerStatus return 500 on HIP connection exception" in {
      val result = controller.getRatepayerStatus("test_hip_connection_error")(FakeRequest())
      status(result)          shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe """[{"code":"ACTION_FAILED","reason":"HIP connection error details"}]"""
    }

  }
