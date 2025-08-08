package uk.gov.hmrc.ngrnotify.backend.controllers

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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.backend.testUtils.HipTestData.testHipHeaders
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.controllers.HipController

import scala.concurrent.Future

class HipControllerSpec extends AsyncWordSpec with Matchers with MockitoSugar {

  val mockHipConnector: HipConnector = mock[HipConnector]
  val controllerComponents: ControllerComponents = Helpers.stubControllerComponents()
  val controller = new HipController(mockHipConnector, controllerComponents)(scala.concurrent.ExecutionContext.global)
  val headers: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(testHipHeaders)

  "hipHelloWorld()" should {
    "return OK with response from HipConnector" in {
      val jsonResponse = Json.obj("message" -> "Hello World")
      when(mockHipConnector.callHelloWorld(testHipHeaders)).thenReturn(Future.successful(jsonResponse))

      val result = controller.hipHelloWorld()(headers)
      status(result) shouldBe OK
      contentAsString(result) shouldBe "Response was: {\"message\":\"Hello World\"}"
    }
  }

  "hipPersonDetails()" should {
    "return OK with person details from HipConnector" in {
      val jsonResponse = Json.obj("name" -> "John Doe")
      when(mockHipConnector.callPersonDetails(testHipHeaders)).thenReturn(Future.successful(jsonResponse))

      val result = controller.hipPersonDetails()(headers)
      status(result) shouldBe OK
      contentAsString(result) shouldBe "Response was: {\"name\":\"John Doe\"}"
    }
  }

  "hipItems()" should {
    "return OK with items from HipConnector" in {
      val jsonResponse = Json.obj("items" -> "item1")
      when(mockHipConnector.callItems(testHipHeaders)).thenReturn(Future.successful(jsonResponse))

      val result = controller.hipItems()(headers)
      status(result) shouldBe OK
      contentAsString(result) shouldBe "Response was: {\"items\":\"item1\"}"
    }
  }
}

