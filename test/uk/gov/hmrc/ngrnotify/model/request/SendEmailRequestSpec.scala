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

package uk.gov.hmrc.ngrnotify.model.request

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import java.util.UUID

/**
  * @author Yuriy Tumakha
  */
class SendEmailRequestSpec extends AnyWordSpec with Matchers:

  "Model SendEmailRequest" should {
    "be serialized/deserialized from JSON" in {
      val sendEmailRequest = SendEmailRequest(
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

      val json = Json.toJson(sendEmailRequest)
      json.as[SendEmailRequest] shouldBe sendEmailRequest
    }
  }
