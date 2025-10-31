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

package uk.gov.hmrc.ngrnotify.model.ratepayer

import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrnotify.model.CommonFormValidators

final case class PhoneNumber(value: String)

object PhoneNumber extends CommonFormValidators {

  implicit val format: OFormat[PhoneNumber] = Json.format[PhoneNumber]

  private lazy val phoneNumberEmptyError    = "phoneNumber.empty.error"
  private lazy val phoneNumberInvalidFormat = "phoneNumber.invalidFormat.error"
  private val phoneNumber                   = "phoneNumber-value"

  def unapply(phoneNumber: PhoneNumber): Option[String] = Some(phoneNumber.value)

  def form(): Form[PhoneNumber] =
    Form(
      mapping(
        phoneNumber -> text().transform[String](_.replaceAll("\\s", ""), identity)
          .verifying(
            firstError(
              isNotEmpty(phoneNumber, phoneNumberEmptyError),
              regexp(phoneNumberRegexPattern.pattern(), phoneNumberInvalidFormat)
            )
          )
      )(PhoneNumber.apply)(PhoneNumber.unapply)
    )

}
