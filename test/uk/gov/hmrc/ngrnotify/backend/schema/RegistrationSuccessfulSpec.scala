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

package uk.gov.hmrc.ngrnotify.backend.schema

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.ngrnotify.model.email.{AddPropertyRequestSent, RegistrationSuccessful}

class RegistrationSuccessfulSpec extends AnyFlatSpec with Matchers {

  val registrationSuccessful = RegistrationSuccessful("Jane", "Jones", "0987654321")

  "Registration successful" should "return the first name" in {
    val result = registrationSuccessful.firstName
    result shouldBe "Jane"
  }

  "Registration successful" should "return the last name" in {
    val result = registrationSuccessful.lastName
    result shouldBe "Jones"
  }

  "Registration successful" should "return the reference number" in {
    val result = registrationSuccessful.reference
    result shouldBe "0987654321"
  }
}
