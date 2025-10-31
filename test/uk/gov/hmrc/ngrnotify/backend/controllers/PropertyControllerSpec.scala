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
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsJson}
import play.api.{Application, inject}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.controllers.routes
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeRequest
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{CredId, PropertyLinkingRequest, VMVProperty}

import scala.concurrent.Future

class PropertyControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
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


  "PropertyController" - {
    "returns OK for a valid request" in {
      val vmvProperty = VMVProperty(100L, "property-id", "address", "LA123", List())

      val propertyLinkingRequest = PropertyLinkingRequest(
        credId = CredId("some-cred-id"),
        vmvProperty = vmvProperty
      )
      val json = Json.toJson(propertyLinkingRequest)

      when(mockHipConnector.submitPropertyLinkingChanges(any[BridgeRequest])(using any[Request[AnyContent]]))
        .thenReturn(
          Future.successful(HttpResponse(OK, ""))
        )

      val request = FakeRequest(POST, routes.PropertyController.submit().url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual ACCEPTED

    }

    Seq(INTERNAL_SERVER_ERROR, BAD_REQUEST) foreach { statusCode =>
      s"return $statusCode for a valid request but Hip returns $statusCode" in {
        val vmvProperty = VMVProperty(100L, "property-id", "address", "LA123", List())

        val propertyLinkingRequest = PropertyLinkingRequest(
          credId = CredId("some-cred-id"),
          vmvProperty = vmvProperty
        )
        val json = Json.toJson(propertyLinkingRequest)

        when(mockHipConnector.submitPropertyLinkingChanges(any[BridgeRequest])(using any[Request[AnyContent]]))
          .thenReturn(
            Future.successful(HttpResponse(statusCode, ""))
          )

        val request = FakeRequest(POST, routes.PropertyController.submit().url)
          .withJsonBody(json)

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual statusCode

      }
    }

    "returns BadRequest for an invalid request" in {
      val json = Json.obj(
        "invalidField" -> "invalidValue"
      )

      val request = FakeRequest(POST, routes.PropertyController.submit().url)
        .withJsonBody(json)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "returns InternalServerError when HipConnector fails" in {
      val vmvProperty = VMVProperty(100L, "property-id", "address", "LA123", List())
      val propertyLinkingRequest = PropertyLinkingRequest(
        credId = CredId("some-cred-id"),
        vmvProperty = vmvProperty
      )
      val json = Json.toJson(propertyLinkingRequest)
      when(mockHipConnector.submitPropertyLinkingChanges(any[BridgeRequest])(using any[Request[AnyContent]]))
        .thenReturn(
          Future.failed(new Exception("HipConnector failure"))
        )
      val request = FakeRequest(POST, routes.PropertyController.submit().url)
        .withJsonBody(json)
      val result: Future[Result] = route(app, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR

    }
  }
}