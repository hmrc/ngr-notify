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

package uk.gov.hmrc.ngrnotify.backend.model.email

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.email.RegistrationSuccessful

class RegistrationSuccessfulSpec extends AnyFlatSpec with Matchers {

  val registrationSuccessful: RegistrationSuccessful = RegistrationSuccessful("Jane", "Jones", "0987654321")

  val registrationSuccessfulJson: JsValue = Json.parse(
    """
      |{
      |"firstName": "Jane",
      |"lastName": "Jones",
      |"reference": "0987654321"
      |}
      |""".stripMargin
  )

  "registrationSuccessful deserialize" should
    "deserialize to json" in {
      Json.toJson(registrationSuccessful) mustBe registrationSuccessfulJson
    }

  "registrationSuccessful serialize" should
    "serialize to json" in {
      registrationSuccessfulJson.as[RegistrationSuccessful] mustBe registrationSuccessful
    }
}
