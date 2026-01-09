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

import play.api.{Application, inject}
import play.api.http.Status.*
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordControllerSpec
import uk.gov.hmrc.ngrnotify.backend.controllers.actions.FakeIdentifierAuthAction
import uk.gov.hmrc.ngrnotify.controllers.RatepayerController
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.model.email.Email
import uk.gov.hmrc.ngrnotify.model.ratepayer.*
import uk.gov.hmrc.ngrnotify.model.ratepayer.AgentStatus.agent
import uk.gov.hmrc.ngrnotify.model.ratepayer.RatepayerType.{individual, organization}
import uk.gov.hmrc.ngrnotify.model.ratepayer.RegisterRatepayerRequest.format
import uk.gov.hmrc.ngrnotify.model.{Address, Postcode}

import java.io.IOException

class RatepayerControllerSpec extends AnyWordControllerSpec:

  override def fakeApplication(): Application = {
    /*
     * The subject of this test is the controller, which depends on the connector,
     * which in turn depends on the HTTP client, which finally talks to the Bridge API service.
     *
     *
     *      <<subject>>         <<dependency>>       <<dependency>>
     *    +-------------+     +---------------+     +--------------+     +--------------------+
     *    | Controller  | --> |   Connector   | --> |  HttpClient  | --> | Bridge API service |
     *    +-------------+     +---------------+     +--------------+     +--------------------+
     *         REAL                 REAL                FAKE
     *
     */
    val httpClient = mock[HttpClientV2]
    // DO NOT instruct the mock here, rather instruct it for each of the test cases below.

    new GuiceApplicationBuilder()
      .overrides(
        bind[HttpClientV2].to(httpClient),
        bind[IdentifierAction].to[FakeIdentifierAuthAction]
      )
      .build()
  }

  "the RatepayerController" when {

    "registering a ratepayer" should {

      "deal with invalid identifiers" in {
        val ratepayerId = "1234567891255" // too many digits!

        // The mock HTTP client is instructed here to return a response with status 400 (Bad Request)
        // when the controller tries to register a ratepayer with an invalid identifier.
        val httpClient = inject[HttpClientV2]
        httpClient
          .whenGetting(s"/job/ratepayers/$ratepayerId")
          .thenReturn(rightResponseWith(BAD_REQUEST, Some("bridge/ratepayer-invalid.json")))

        val controller = inject[RatepayerController]
        val request    = FakeRequest("POST", "/").withHeaders("X-Provider-Id" -> ratepayerId).withBody(Json.toJson(
          RegisterRatepayerRequest(Some(individual))
        ))

        val result = controller.registerRatepayer(request)
        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(
          result
        )              shouldBe """[{"code":"ACTION_FAILED","reason":"Invalid format for Id – provided identifier does not match the expected pattern."}]"""
      }

      "deal with existing person" in {
        // This is the UPDATE ratepayer scenario
        val ratepayerId = "123456789123"

        //
        // This scenario requires additional instructions to be given to the mock HTTP client
        // as the interaction with the Bridge API service is more complex than a simple request/response pass.
        //
        val httpClient = inject[HttpClientV2]
        httpClient
          .whenGetting(s"/job/ratepayers/$ratepayerId")
          .thenReturn(rightResponseWith(OK, Some("bridge/ratepayer-found.json")))
        httpClient
          .whenPosting("/job")
          .thenReturn(rightResponseWith(NO_CONTENT, None))

        val controller = inject[RatepayerController]
        val request    = FakeRequest("POST", "/")
          .withHeaders("X-Provider-Id" -> ratepayerId)
          .withBody(Json.toJson(
          RegisterRatepayerRequest(
            userType = Some(organization),
            agentStatus = Some(agent),
            name = Some(Name("David Smith")),
            tradingName = None,
            email = Some(Email("david.smith@some.com")),
            nino = Some(Nino("QQ123456A")),
            contactNumber = Some(PhoneNumber("1111")),
            secondaryNumber = None,
            address = Some(Address("Line 1", Some("Line 2"), "City", None, Postcode("ZZ11 1ZZ"))),
            trnReferenceNumber = Some(TRNReferenceNumber(ReferenceType.TRN, "TRN123456")),
            isRegistered = Some(false),
            recoveryId = Some("AAH4-KKSW-7LX9")
          )
        ))

        val result = controller.registerRatepayer(request)
        contentAsString(result) shouldBe empty
        status(result)          shouldBe ACCEPTED
      }
    }

    "dealing with property links" should {

      ".getRatepayerPropertyLinks return 500 for wrong ID" in {
        val ratepayerId = "1234567891255"
        val httpClient = inject[HttpClientV2]
        httpClient
          .whenGetting(s"/ratepayers/$ratepayerId")
          .thenReturn(rightResponseWith(BAD_REQUEST, Some("ratepayerWrongID.json")))

        val controller = inject[RatepayerController]
        val result     = controller.getRatepayerPropertyLinks(FakeRequest().withHeaders("X-Provider-Id" -> ratepayerId))
        status(result)        shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) should include("Invalid format for Id")
      }

      ".getRatepayerPropertyLinks return 500 for invalid json in HIP response" in {
        val ratepayerId         = "1234567890123456780"
        val httpClient = inject[HttpClientV2]
        httpClient
          .whenGetting(s"/ratepayers/$ratepayerId")
          .thenReturn(rightResponseWith(OK, Some("empty-object.json")))

        val controller = inject[RatepayerController]
        val result     = controller.getRatepayerPropertyLinks(FakeRequest().withHeaders("X-Provider-Id" -> ratepayerId))
        status(result)          shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe """[{"code":"JSON_VALIDATION_ERROR","reason":"/job <- error.path.missing"}]"""
      }

      ".getRatepayerPropertyLinks return 500 if HIP response body is not a JSON" in {
        val ratepayerId = "test_no_json"
        val httpClient = inject[HttpClientV2]
        httpClient
          .whenGetting(s"/ratepayers/$ratepayerId")
          .thenReturn(rightResponseWith(OK))

        val controller = inject[RatepayerController]
        val result     = controller.getRatepayerPropertyLinks(FakeRequest().withHeaders("X-Provider-Id" -> ratepayerId))
        status(result)          shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe """[{"code":"WRONG_RESPONSE_BODY","reason":"HIP response could not be parsed into JSON format."}]"""
      }

      ".getRatepayerPropertyLinks return 500 on HIP connection exception" in {
        val ratepayerId = "test_hip_connection_error"
        val httpClient = inject[HttpClientV2]
        httpClient
          .whenGetting(s"/ratepayers/$ratepayerId")
          .thenReturn(leftResponseWith(IOException("HIP connection error details")))

        val controller = inject[RatepayerController]
        val result     = controller.getRatepayerPropertyLinks(FakeRequest().withHeaders("X-Provider-Id" -> ratepayerId))
        status(result)          shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe """[{"code":"ACTION_FAILED","reason":"HIP connection error details"}]"""
      }
    }
  }
