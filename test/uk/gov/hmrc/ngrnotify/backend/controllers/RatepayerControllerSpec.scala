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
import uk.gov.hmrc.ngrnotify.controllers.RatepayerController
import uk.gov.hmrc.ngrnotify.model.Address
import uk.gov.hmrc.ngrnotify.model.email.Email
import uk.gov.hmrc.ngrnotify.model.ratepayer.AgentStatus.agent
import uk.gov.hmrc.ngrnotify.model.ratepayer.RatepayerType.organization
import uk.gov.hmrc.ngrnotify.model.ratepayer.{Name, Nino, PhoneNumber, RegisterRatepayerRequest}

import java.io.IOException

class RatepayerControllerSpec extends AnyWordAppSpec:

  private val controller = inject[RatepayerController]

  given Materializer = NoMaterializer

  override def fakeApplication(): Application =
    val httpClientV2Mock = mock[HttpClientV2]

    when(
      httpClientV2Mock.post(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK), """"OK""""))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/GGID123345"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK), testResourceContent("ratepayerHasPropertyLink.json")))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/1234567891255"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(BAD_REQUEST), testResourceContent("ratepayerWrongID.json")))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/test_invalid_json"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK), "{}"))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/test_no_json"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(OK)))

    when(
      httpClientV2Mock.get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/test_hip_connection_error"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Left(IOException("HIP connection error details"))))

    new GuiceApplicationBuilder()
      .overrides(bind[HttpClientV2].to(httpClientV2Mock))
      .build()

  "RatepayerController" should {
    ".registerRatepayer return 202" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.toJson(
          RegisterRatepayerRequest(
            "login",
            Some(organization),
            Some(agent),
            Some(Name("Full name")),
            None,
            Some(Email("test@email.com")),
            Some(Nino("QQ123456A")),
            Some(PhoneNumber("1111")),
            None,
            Some(Address("Line 1", Some("Line 2"), "City", None, "ZZ11 1ZZ"))
          )
        ))

      val result = controller.registerRatepayer(fakeRequest)
      status(result)          shouldBe ACCEPTED
      contentAsString(result) shouldBe """{"status":"OK"}"""
    }

    ".registerRatepayer return 400" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")

      val result = controller.registerRatepayer(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    ".getRatepayerPropertyLinks return 200" in {
      val result = controller.getRatepayerPropertyLinks("GGID123345")(FakeRequest())
      status(result)          shouldBe OK
      contentAsString(result) shouldBe """{"linked":true,"properties":["88, Anderton Close, Tavistock, Devon, PL22 8DE"]}"""
    }

    ".getRatepayerPropertyLinks return 500 for wrong ID" in {
      val result = controller.getRatepayerPropertyLinks("1234567891255")(FakeRequest())
      status(result)        shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) should include("Invalid format for Id")
    }

    ".getRatepayerPropertyLinks return 500 for invalid json in HIP response" in {
      val result = controller.getRatepayerPropertyLinks("test_invalid_json")(FakeRequest())
      status(result)          shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe """[{"code":"JSON_VALIDATION_ERROR","reason":"/job <- error.path.missing"}]"""
    }

    ".getRatepayerPropertyLinks return 500 if HIP response body is not a JSON" in {
      val result = controller.getRatepayerPropertyLinks("test_no_json")(FakeRequest())
      status(result)          shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe """[{"code":"WRONG_RESPONSE_BODY","reason":"HIP response could not be parsed into JSON format."}]"""
    }

    ".getRatepayerPropertyLinks return 500 on HIP connection exception" in {
      val result = controller.getRatepayerPropertyLinks("test_hip_connection_error")(FakeRequest())
      status(result)          shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe """[{"code":"ACTION_FAILED","reason":"HIP connection error details"}]"""
    }

  }
