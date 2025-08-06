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
import uk.gov.hmrc.ngrnotify.connector.EmailConnector
import uk.gov.hmrc.ngrnotify.model.EmailTemplate
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.{ngr_add_property_request_sent, ngr_registration_successful}
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification
import uk.gov.hmrc.ngrnotify.model.email.{AddPropertyRequestSent, RegistrationSuccessful}

import java.util.UUID
//import uk.gov.hmrc.ngrnotify.backend.schema.Address

import java.time.temporal.ChronoUnit.MILLIS
import java.time.{Instant, LocalDate}

trait FakeObjects {
  val token: String = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik="

  val prefilledRegistrationSuccessful: RegistrationSuccessful = RegistrationSuccessful("John", "Doe", "123456789")
  val prefilledAddPropertyRequest: AddPropertyRequestSent     = AddPropertyRequestSent("John", "Doe", "123456789", "0AA")

  val prefilledFakeFirstName = "John"
  val prefilledFakeLastName  = "Doe"
  val prefilledFakeEmail     = "test@email.com"
  val prefilledFakeEmail2    = "test2@email.com"

  val prefilledDateInput: LocalDate = LocalDate.of(2022, 6, 1)
  val today: LocalDate              = LocalDate.now

  val templateParamsJsonRegistration = Json.obj(
    "firstName" -> "David",
    "lastName"  -> "Jones",
    "reference" -> "REG12345"
  )

  val templateParamsJsonAddProperty = Json.obj(
    "firstName"         -> "David",
    "lastName"          -> "Jones",
    "reference"         -> "REG12345",
    "postcodeEndString" -> "0AA"
  )

  val bodyJsonRegistrationSuccessful = Json.obj(
    "emailTemplateId" -> ngr_registration_successful,
    "trackerId"       -> UUID.randomUUID(),
    "sendToEmails"    -> Seq(prefilledFakeEmail, prefilledFakeEmail2),
    "templateParams"  -> templateParamsJsonRegistration,
    "callbackUrl"     -> "123123123"
  )

  val bodyJsonAddProperty = Json.obj(
    "emailTemplateId" -> ngr_add_property_request_sent,
    "trackerId"       -> UUID.randomUUID(),
    "sendToEmails"    -> Seq(prefilledFakeEmail, prefilledFakeEmail2),
    "templateParams"  -> templateParamsJsonAddProperty,
    "callbackUrl"     -> "123123123"
  )

  val bodyJsonAddPropertyRequestSent =
    """{
      |  "trackerId": "9d2dee33-7803-485a-a2b1-2c7538e597ee",
      |  "sendToEmails": [
      |    "test1@email.com",
      |    "test2@email.com"
      |  ],
      |  "callbackUrl": "http://localhost:1501/ngr-stub/callback",
      |  "templateParams": {
      |    "firstName": "David",
      |    "lastName": "Jones",
      |    "reference": "REG12345",
      |    "postcodeFirstPart": "AA1"
      |  }
      |}""".stripMargin

  val baseFilledAddPropertyRequest: AddPropertyRequestSent =
    AddPropertyRequestSent("John", "Smith", "1234567890", "0AA")

  val baseFilledRegistrationSuccessful: RegistrationSuccessful =
    RegistrationSuccessful("John", "Smith", "1234567890")

  val prefilledEmailRegistrationSuccessful: EmailNotification = EmailNotification(
    emailTemplateId = ngr_registration_successful,
    trackerId = UUID.randomUUID(),
    sendToEmails = Seq(prefilledFakeEmail, prefilledFakeEmail2),
    templateParams = templateParamsJsonRegistration,
    callbackUrl = Some("abc"),
    client = Some("xyz"),
    _id = ObjectId("666f6f2d6261722d71757578"),
    createdAt = Instant.now()
  )

  val prefilledEmailAddProperty: EmailNotification = EmailNotification(
    emailTemplateId = ngr_add_property_request_sent,
    trackerId = UUID.randomUUID(),
    sendToEmails = Seq(prefilledFakeEmail, prefilledFakeEmail2),
    templateParams = bodyJsonAddProperty,
    callbackUrl = Some("abc"),
    client = Some("xyz"),
    _id = ObjectId("666f6f2d6261722d71757578"),
    createdAt = Instant.now()
  )

//  val prefilledMonthYearInput: MonthsYearDuration = MonthsYearDuration(6, 2000)

//  val baseFilledConnectedSubmission: EmailNotification = EmailNotification(bodyJsonRegistrationSuccessful)
//
//
//  val prefilledConnectedSubmission: EmailNotification = baseFilledConnectedSubmission.copy(
//    emailTemplateId = ngr_registration_successful,
//    trackerId = UUID.randomUUID(),
//    sendToEmails = Seq["test1@mail.com"],
//    templateParams = bodyJsonRegistrationSuccessful,
//    callbackUrl = None,
//    client = None,
//    _id = ???,
//    createdAt = ???
//  )
//
//
//  def createEmailNotification(): EmailNotification =
//    EmailNotification(prefilledConnectedSubmission)
}
