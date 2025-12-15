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
import uk.gov.hmrc.ngrnotify.backend.controllers.actions.FakeIdentifierAuthAction
import uk.gov.hmrc.ngrnotify.connectors.bridge.{BridgeConnector, FutureEither}
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.controllers.routes
import uk.gov.hmrc.ngrnotify.model.propertyDetails.*

import java.time.LocalDate
import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PhysicalControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach:

  val mockBridgeConnector: BridgeConnector = mock[BridgeConnector]
  val assessmentId                         = AssessmentId("assessmentId123")

  override def beforeEach(): Unit = {
    reset(mockBridgeConnector)
    super.beforeEach()
  }

  override lazy val app: Application =
    GuiceApplicationBuilder()
      .overrides(
        inject.bind[BridgeConnector].toInstance(mockBridgeConnector),
        inject.bind[IdentifierAction].to[FakeIdentifierAuthAction]
      )
      .build()

  "PhysicalController" - {
    ".updatePhysicalPropertyChanges return 202" in {
      val json = Json.toJson(
        PropertyChangesRequest(
          LocalDate.of(2023, 1, 1),
          Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
          Seq(("airConditioning", "none"), ("securityCamera", "23")),
          Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
          Some(AnythingElseData(true, Some("addtional text"))),
          Seq("uploadId1", "uploadId2")
        )
      )

      when(
        mockBridgeConnector.submitPhysicalPropertyChanges(any[CredId], any[AssessmentId], any[PropertyChangesRequest])(using any[Request[?]])
      ).thenReturn(
        FutureEither(Future.successful(Right(NO_CONTENT)))
      )

      val request = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges(assessmentId = assessmentId).url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) shouldBe ACCEPTED
    }

    s"return $INTERNAL_SERVER_ERROR for a valid request but Hip returns $INTERNAL_SERVER_ERROR" in {
      val json = Json.toJson(
        PropertyChangesRequest(
          LocalDate.of(2023, 1, 1),
          Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
          Seq(("airConditioning", "none"), ("securityCamera", "23")),
          Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
          Some(AnythingElseData(true, Some("addtional text"))),
          Seq("uploadId1", "uploadId2")
        )
      )

      when(mockBridgeConnector.submitPhysicalPropertyChanges(any[CredId], any[AssessmentId], any[PropertyChangesRequest])(using any[Request[?]]))
        .thenReturn(
          FutureEither(Future.successful(Left(INTERNAL_SERVER_ERROR)))
        )

      val request = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges(assessmentId = assessmentId).url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

    }

    "returns BadRequest for an invalid request" in {
      val json = Json.obj(
        "invalidField" -> "invalidValue"
      )

      val request = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges(assessmentId = assessmentId).url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "returns InternalServerError when HipConnector fails" in {
      val json = Json.toJson(
        PropertyChangesRequest(
          LocalDate.of(2023, 1, 1),
          Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
          Seq(("airConditioning", "none"), ("securityCamera", "23")),
          Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
          Some(AnythingElseData(true, Some("addtional text"))),
          Seq("uploadId1", "uploadId2")
        )
      )

      when(mockBridgeConnector.submitPhysicalPropertyChanges(any[CredId], any[AssessmentId], any[PropertyChangesRequest])(using any[Request[?]]))
        .thenReturn(FutureEither(Future.successful(Left("Exception occurred"))))

      val request                = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges(assessmentId = assessmentId).url)
        .withJsonBody(json)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "returns InternalServerError when an exception is thrown" in {
      val json = Json.toJson(
        PropertyChangesRequest(
          LocalDate.of(2023, 1, 1),
          Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
          Seq(("airConditioning", "none"), ("securityCamera", "23")),
          Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
          Some(AnythingElseData(true, Some("addtional text"))),
          Seq("uploadId1", "uploadId2")
        )
      )

      when(mockBridgeConnector.submitPhysicalPropertyChanges(any[CredId], any[AssessmentId], any[PropertyChangesRequest])(using any[Request[?]]))
        .thenReturn(FutureEither(Future.successful(Left("Exception occurred"))))

      val request                = FakeRequest(POST, routes.PhysicalController.updatePropertyChanges(assessmentId = assessmentId).url)
        .withJsonBody(json)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR

    }

  }
