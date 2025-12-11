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
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Request, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import play.api.{Application, inject}
import uk.gov.hmrc.ngrnotify.backend.controllers.actions.FakeIdentifierAuthAction
import uk.gov.hmrc.ngrnotify.connectors.bridge.{BridgeConnector, BridgeResult, FutureEither, NoContent}
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.controllers.routes
import uk.gov.hmrc.ngrnotify.model.propertyDetails.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RaldControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val mockBridgeConnector: BridgeConnector = mock[BridgeConnector]
  val assessmentId = AssessmentId("assessmentId123")

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


  "RaldController" - {
    ".updateRaldChanges return 202" in {

      val json: JsObject = Json.obj(
        "field" -> "value"
      )

      val success: BridgeResult[NoContent] =
        new FutureEither(Future.successful(Right(())))  // NoContent = Unit

      when(
        mockBridgeConnector.submitRaldChanges(
          any[CredId],
          any[AssessmentId],
          any[JsObject]
        )(using any[Request[?]])
      ).thenReturn(success)

      val request = FakeRequest(POST, routes.RaldController.updateRaldChanges(assessmentId).url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) shouldBe ACCEPTED
    }


    Seq(INTERNAL_SERVER_ERROR).foreach { statusCode =>
      s"return $statusCode for a valid request but Hip returns $statusCode" in {

        val json: JsObject = Json.obj("field" -> "value")

        val failure: BridgeResult[NoContent] =
          new FutureEither(Future.successful(Left(s"Simulated failure: $statusCode")))

        when(
          mockBridgeConnector.submitRaldChanges(
            any[CredId],
            any[AssessmentId],
            any[JsObject]
          )(using any[Request[?]])
        ).thenReturn(failure)

        val request = FakeRequest(POST, routes.RaldController.updateRaldChanges(assessmentId).url)
          .withJsonBody(json)

        val result: Future[Result] = route(app, request).value

        status(result) shouldBe statusCode
      }
    }


  }

}
