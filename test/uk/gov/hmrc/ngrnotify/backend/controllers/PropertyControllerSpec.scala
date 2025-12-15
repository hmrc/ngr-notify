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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.{Application, inject}
import uk.gov.hmrc.ngrnotify.backend.controllers.actions.FakeIdentifierAuthAction
import uk.gov.hmrc.ngrnotify.connectors.bridge.{BridgeConnector, FutureEither}
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.controllers.routes
import uk.gov.hmrc.ngrnotify.model.propertyDetails.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PropertyControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach with PropertyLinkingData:

  val mockBridgeConnector: BridgeConnector = mock[BridgeConnector]
  val assessmentId: AssessmentId           = AssessmentId("assessmentId123")

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

  "PropertyController" - {
    ".updatePropertyChanges return 202" in {

      when(mockBridgeConnector.submitPropertyChanges(any[CredId], any[AssessmentId], any[PropertyLinkingRequest])(using any[Request[?]]))
        .thenReturn(
          FutureEither(Future.successful(Right(NO_CONTENT)))
        )

      val request = FakeRequest(POST, routes.PropertyController.updatePropertyChanges().url)
        .withJsonBody(propertyLinkingRequestJson)

      val result: Future[Result] = route(app, request).value

      status(result) shouldBe ACCEPTED
    }

    s"return $INTERNAL_SERVER_ERROR for a valid request but Hip returns $INTERNAL_SERVER_ERROR" in {

      when(mockBridgeConnector.submitPropertyChanges(any[CredId], any[AssessmentId], any[PropertyLinkingRequest])(using any[Request[?]]))
        .thenReturn(
          FutureEither(Future.successful(Left(INTERNAL_SERVER_ERROR)))
        )

      val request = FakeRequest(POST, routes.PropertyController.updatePropertyChanges().url)
        .withJsonBody(propertyLinkingRequestJson)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "returns BadRequest for an invalid request" in {
      val json = Json.obj(
        "invalidField" -> "invalidValue"
      )

      val request = FakeRequest(POST, routes.PropertyController.updatePropertyChanges().url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "returns InternalServerError when HipConnector fails" in {

      when(mockBridgeConnector.submitPropertyChanges(any[CredId], any[AssessmentId], any[PropertyLinkingRequest])(using any[Request[?]]))
        .thenReturn(FutureEither(Future.successful(Left("Exception occurred"))))

      val request                = FakeRequest(POST, routes.PropertyController.updatePropertyChanges().url)
        .withJsonBody(propertyLinkingRequestJson)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "returns InternalServerError when an exception is thrown" in {
      when(mockBridgeConnector.submitPropertyChanges(any[CredId], any[AssessmentId], any[PropertyLinkingRequest])(using any[Request[?]]))
        .thenReturn(FutureEither(Future.successful(Left("Exception occurred"))))

      val request                = FakeRequest(POST, routes.PropertyController.updatePropertyChanges().url)
        .withJsonBody(propertyLinkingRequestJson)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR

    }
  }

trait PropertyLinkingData {
  val credId: CredId                             = CredId("test-cred-id")

  val valuations                                 = List(
    Valuation(
      assessmentRef = 12345L,
      assessmentStatus = "CURRENT",
      rateableValue = None,
      scatCode = Some("Details about valuation"),
      descriptionText = "description",
      effectiveDate = java.time.LocalDate.now(),
      currentFromDate = java.time.LocalDate.now(),
      listYear = "2023",
      primaryDescription = "primary",
      allowedActions = List.empty,
      listType = "type",
      propertyLinkEarliestStartDate = None
    )
  )
  val vmvProperty: VMVProperty                   = VMVProperty(100L, "property-id", "address", "LA123", valuations)
  val currentRatepayer: Option[CurrentRatepayer] = Some(CurrentRatepayer(true, Some("John Doe")))

  val propertyLinkingRequest              = PropertyLinkingRequest(
    vmvProperty = vmvProperty,
    currentRatepayer = currentRatepayer,
    businessRatesBill = Some("bill.pdf"),
    connectionToProperty = Some("Owner"),
    requestSentReference = Some("ref-123"),
    evidenceDocument = Some("evidence.pdf"),
    evidenceDocumentUrl = Some("http://example.com/evidence.pdf"),
    evidenceDocumentUploadId = Some("upload-123"),
    uploadEvidence = Some("yes")
  )
  val propertyLinkingRequestJson: JsValue = Json.toJson(propertyLinkingRequest)
}
