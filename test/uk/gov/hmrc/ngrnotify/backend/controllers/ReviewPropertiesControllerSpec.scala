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
import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty}
import play.api.{Application, inject}
import uk.gov.hmrc.ngrnotify.backend.controllers.actions.FakeIdentifierAuthAction
import uk.gov.hmrc.ngrnotify.connectors.bridge.{BridgeConnector, FutureEither}
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.controllers.routes
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{AssessmentId, CredId, JobMessageTestData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReviewPropertiesControllerSpec extends AnyFreeSpec with GuiceOneAppPerSuite with BeforeAndAfterEach with JobMessageTestData:
  val mockBridgeConnector: BridgeConnector = mock[BridgeConnector]
  val assessmentId: AssessmentId = AssessmentId("123L")

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

  "ReviewPropertiesController" - {
    ".properties return 200" in {

      when(mockBridgeConnector.getReviewProperties(any[CredId], any[AssessmentId])(using any[Request[?]]))
        .thenReturn(
          FutureEither(Future.successful(Right((sampleSurveyEntity, None))))
        )

      val request = FakeRequest(GET, routes.ReviewPropertiesController.properties(assessmentId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual OK
    }

    s"return $INTERNAL_SERVER_ERROR for a valid request but Hip returns $INTERNAL_SERVER_ERROR" in {

      when(mockBridgeConnector.getReviewProperties(any[CredId], any[AssessmentId])(using any[Request[?]]))
        .thenReturn(
          FutureEither(Future.successful(Left(INTERNAL_SERVER_ERROR)))
        )

      val request = FakeRequest(GET, routes.ReviewPropertiesController.properties(assessmentId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "returns InternalServerError for an invalid request" in {
      when(mockBridgeConnector.getReviewProperties(any[CredId], any[AssessmentId])(using any[Request[?]]))
        .thenReturn(
          FutureEither(Future.successful(Left(BAD_REQUEST)))
        )
      val request = FakeRequest(GET, routes.ReviewPropertiesController.properties(assessmentId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "returns InternalServerError when HipConnector fails" in {

      when(mockBridgeConnector.getReviewProperties(any[CredId], any[AssessmentId])(using any[Request[?]]))
        .thenReturn(FutureEither(Future.successful(Left("Exception occurred"))))

      val request = FakeRequest(GET, routes.ReviewPropertiesController.properties(assessmentId).url)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "returns InternalServerError when an exception is thrown" in {
      when(mockBridgeConnector.getReviewProperties(any[CredId], any[AssessmentId])(using any[Request[?]]))
        .thenReturn(FutureEither(Future.successful(Left("Exception occurred"))))

      val request = FakeRequest(GET, routes.ReviewPropertiesController.properties(assessmentId).url)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR

    }
  }

