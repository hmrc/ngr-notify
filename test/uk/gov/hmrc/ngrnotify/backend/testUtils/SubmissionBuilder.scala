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

package uk.gov.hmrc.ngrnotify.backend.testUtils

import org.bson.types.ObjectId
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.ngr_registration_successful

import java.time.Instant
import java.util.UUID

object SubmissionBuilder {

  val templateParamsJsonRegistration = Json.obj(
    "firstName" -> "David",
    "lastName"  -> "Jones",
    "reference" -> "REG12345"
  )

  def createEmailNotification(n: Int) = {
    val submissionSuffix = n match {
      case n: Int if n < 9  => s"00$n"
      case n: Int if n < 99 => s"0$n"
      case n: Int           => n.toString
    }
    EmailNotification(
      emailTemplateId = ngr_registration_successful,
      trackerId = UUID.randomUUID(),
      sendToEmails = Seq("test1@email.com", "test2@email.com"),
      templateParams = templateParamsJsonRegistration,
      callbackUrl = Some("abc"),
      client = Some("xyz"),
      _id = ObjectId("666f6f2d6261722d71757578"),
      createdAt = Instant.ofEpochMilli(0)
    )

  }

}
