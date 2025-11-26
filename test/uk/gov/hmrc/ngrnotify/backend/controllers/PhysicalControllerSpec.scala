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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.{Request, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import play.api.{Application, inject}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.controllers.routes
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeRequest
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{AnythingElseData, ChangeToUseOfSpace, CredId, PropertyChangesRequest}

import java.time.LocalDate
import scala.collection.immutable.Seq
import scala.concurrent.Future

class PhysicalControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach:

  val mockHipConnector: HipConnector = mock[HipConnector]

  override def beforeEach(): Unit = {
    reset(mockHipConnector)
    super.beforeEach()
  }

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .overrides(
        inject.bind[HipConnector].toInstance(mockHipConnector)
      )
      .build()

  "PhysicalController" - {
    ".updatePropertyChanges return 202" in {
      pending
      // TODO restore this test as soon as migrated to the new BridgeConnector
      val json = Json.toJson(
        PropertyChangesRequest(
          CredId("credId"),
          LocalDate.of(2023, 1, 1),
          Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
          Seq(("airConditioning", "none"), ("securityCamera", "23")),
          Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
          Some(AnythingElseData(true, Some("addtional text"))),
          Seq("uploadId1", "uploadId2")
        )
      )

      when(mockHipConnector.updatePropertyChanges(any[BridgeRequest])(using any[Request[?]]))
        .thenReturn(
          Future.successful(HttpResponse(OK, ""))
        )

      val request = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges().url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) shouldBe ACCEPTED
    }

    Seq(INTERNAL_SERVER_ERROR, BAD_REQUEST) foreach { statusCode =>
      s"return $statusCode for a valid request but Hip returns $statusCode" in {
        pending
        // TODO restore this test as soon as migrated to the new BridgeConnector
        val json = Json.toJson(
          PropertyChangesRequest(
            CredId("credId"),
            LocalDate.of(2023, 1, 1),
            Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
            Seq(("airConditioning", "none"), ("securityCamera", "23")),
            Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
            Some(AnythingElseData(true, Some("addtional text"))),
            Seq("uploadId1", "uploadId2")
          )
        )

        when(mockHipConnector.updatePropertyChanges(any[BridgeRequest])(using any[Request[?]]))
          .thenReturn(
            Future.successful(HttpResponse(statusCode, ""))
          )

        val request = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges().url)
          .withJsonBody(json)

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual statusCode

      }
    }

    "returns BadRequest for an invalid request" in {
      val json = Json.obj(
        "invalidField" -> "invalidValue"
      )

      val request = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges().url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "returns InternalServerError when HipConnector fails" in {
      pending
      // TODO restore this test as soon as migrated to the new BridgeConnector
      val json = Json.toJson(
        PropertyChangesRequest(
          CredId("credId"),
          LocalDate.of(2023, 1, 1),
          Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
          Seq(("airConditioning", "none"), ("securityCamera", "23")),
          Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
          Some(AnythingElseData(true, Some("addtional text"))),
          Seq("uploadId1", "uploadId2")
        )
      )

      when(mockHipConnector.updatePropertyChanges(any[BridgeRequest])(using any[Request[?]]))
        .thenReturn(
          Future.failed(new Exception("HipConnector failure"))
        )

      val request                = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges().url)
        .withJsonBody(json)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR

    }

  }
