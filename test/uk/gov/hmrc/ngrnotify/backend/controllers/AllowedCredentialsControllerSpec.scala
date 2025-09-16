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

import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import play.api.test.Helpers.*
import play.api.test.FakeRequest
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.controllers.AllowedCredentialsController
import scala.concurrent.Future
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.ngrnotify.connectors.AllowedCredentialsConnector

class AllowedCredentialsControllerSpec extends AnyWordAppSpec {

  private val mockConnector = mock[AllowedCredentialsConnector]
  private val cc            = inject[ControllerComponents]
  private val controller    = new AllowedCredentialsController(mockConnector, cc)(using scala.concurrent.ExecutionContext.global)

  "AllowedCredentialsController" should {

    "return 200 for allowed id with true" in {
      when(mockConnector.isAllowed("testId-1")).thenReturn(Future.successful(true))

      val result = controller.isAllowedInPrivateBeta("testId-1").apply(FakeRequest())

      status(result)        shouldBe OK
      contentAsJson(result) shouldBe Json.obj("allowed" -> true)
    }

    "return 200 for not allowed id with false" in {
      when(mockConnector.isAllowed("testId-2")).thenReturn(Future.successful(false))

      val result = controller.isAllowedInPrivateBeta("testId-2").apply(FakeRequest())

      status(result)        shouldBe OK
      contentAsJson(result) shouldBe Json.obj("allowed" -> false)
    }

    "return 500" in {
      val exception = new RuntimeException("Service unavailable")
      when(mockConnector.isAllowed("testId-3")).thenReturn(Future.failed(exception))

      val result = controller.isAllowedInPrivateBeta("testId-3").apply(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
