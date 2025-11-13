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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsNull, JsObject, Json}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers, Injecting}
import uk.gov.hmrc.ngrnotify.controllers.EmailSenderController
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.*
import uk.gov.hmrc.ngrnotify.model.request.SendEmailRequest

import java.util.UUID

class EmailSenderControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with Injecting:

  private val controller = inject[EmailSenderController]

  given Materializer = NoMaterializer

  "EmailSenderController" should {
    "return 201 with registration successful" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.toJson(
          SendEmailRequest(
            UUID.fromString("9d2dee33-7803-485a-a2b1-2c7538e597ee"),
            Seq("test1@email.com", "test2@email.com"),
            Json.obj(
              "firstName"       -> "David",
              "lastName"        -> "Jones",
              "reference"       -> "REG12345",
              "postcodeEndPart" -> "0AA"
            ),
            Some("http://localhost:1501/ngr-stub/callback")
          )
        ))

      val result = controller.sendEmail(ngr_registration_successful.toString)(fakeRequest)
      status(result)        shouldBe CREATED
      contentAsJson(result) shouldBe Json.obj("status" -> "Success", "message" -> "Email dispatch task successfully created.")
    }

    "return 201 with add property request successful" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.toJson(
          SendEmailRequest(
            UUID.fromString("9d2dee33-7803-485a-a2b1-2c7538e597ee"),
            Seq("test1@email.com", "test2@email.com"),
            Json.obj(
              "firstName"       -> "David",
              "lastName"        -> "Jones",
              "reference"       -> "REG12345",
              "postcodeEndPart" -> "0AA"
            ),
            Some("http://localhost:1501/ngr-stub/callback")
          )
        ))

      val result = controller.sendEmail(ngr_add_property_request_sent.toString)(fakeRequest)
      status(result)        shouldBe CREATED
      contentAsJson(result) shouldBe Json.obj("status" -> "Success", "message" -> "Email dispatch task successfully created.")
    }

    "return 400" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")

      val result = controller.sendEmail(ngr_registration_successful.toString)(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "return 400 when invalid body is provided" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.obj())

      val result = controller.sendEmail(ngr_registration_successful.toString)(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "return 400 when invalid template is provided" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.toJson(
          SendEmailRequest(
            trackerId = UUID.fromString("9d2dee33-7803-485a-a2b1-2c7538e597ee"),
            sendToEmails = Seq("test1@email.com", "test2@email.com"),
            templateParams = Json.obj(
              "firstName"       -> "David",
              "lastName"        -> "Jones",
              "reference"       -> "REG12345",
              "postcodeEndPart" -> "0AA"
            ),
            callbackUrl = Some("http://localhost:1501/ngr-stub/callback")
          )
        ))

      val result = controller.sendEmail("template")(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "return 400 when null body is provided" in {
      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(JsNull)

      val result = controller.sendEmail(ngr_registration_successful.toString)(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }
  }
