/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ngrnotify.backend.model.request

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.backend.testUtils.SubmissionBuilder.templateParamsJsonRegistration
import uk.gov.hmrc.ngrnotify.model.request.SendEmailRequest

import java.util.UUID

class SendEmailRequestSpec extends AnyFlatSpec with Matchers {

  val id = "00000000-0000-0000-0000-000000000000"

  val SendEmailRequestSpec: SendEmailRequest =
    SendEmailRequest(
      UUID.fromString(id),
      Seq("test@email.com", "test2@email.com"),
      templateParamsJsonRegistration,
      Some("callback URL")
    )

  val SendEmailRequestJson: JsValue = Json.parse(
    """
      |{
      |"trackerId": "00000000-0000-0000-0000-000000000000",
      |"sendToEmails": ["test@email.com", "test2@email.com"],
      |"templateParams": {"firstName":"David","lastName":"Jones","reference":"REG12345"},
      |"callbackUrl": "callback URL"
      |}
      |""".stripMargin
  )

  "ApiFailure deserialize" should
    "deserialize to json" in {
      Json.toJson(SendEmailRequestSpec) mustBe SendEmailRequestJson
    }

  "ApiFailure serialize" should
    "serialize to json" in {
      SendEmailRequestJson.as[SendEmailRequest] mustBe SendEmailRequestSpec
    }

}
