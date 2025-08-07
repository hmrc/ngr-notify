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

package uk.gov.hmrc.ngrnotify.backend.model.db

import org.bson.types.ObjectId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.backend.testUtils.SubmissionBuilder.templateParamsJsonRegistration
import uk.gov.hmrc.ngrnotify.backend.testUtils.TestData
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.ngr_registration_successful
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification

import java.time.Instant
import java.util.UUID

class EmailNotificationSpec extends AnyFlatSpec with Matchers {

  val id       = "00000000-0000-0000-0000-000000000000"
  val time     = Instant.ofEpochMilli(0)
  val objectId = "000000000000000000000000"

  val EmailNotificationRequest: EmailNotification =
    EmailNotification(
      ngr_registration_successful,
      UUID.fromString(id),
      Seq("test@email.com", "test2@email.com"),
      templateParamsJsonRegistration,
      Some("callback URL"),
      Some("client text"),
      ObjectId(objectId),
      time
    )

  val EmailNotificationJson: JsValue = Json.parse(
    """
      |{
      |"emailTemplateId":"ngr_registration_successful",
      |"trackerId":"00000000-0000-0000-0000-000000000000",
      |"sendToEmails":["test@email.com","test2@email.com"],
      |"templateParams":{"firstName":"David","lastName":"Jones","reference":"REG12345"},
      |"callbackUrl": "callback URL",
      |"client": "client text",
      |"_id":{"$oid":"000000000000000000000000"},
      |"createdAt":{"$date":{"$numberLong":"0"}}
      |}
      |""".stripMargin
  )

  "ApiFailure deserialize" should
    "deserialize to json" in {
      Json.toJson(EmailNotificationRequest) mustBe EmailNotificationJson
    }

  "ApiFailure serialize" should
    "serialize to json" in {
      EmailNotificationJson.as[EmailNotification] mustBe EmailNotificationRequest
    }

}
