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

class AddPropertyRequestSpec extends AnyFlatSpec with Matchers {

  val addPropertyRequest = AddPropertyRequestSent("John", "Smith", "1234567890", "0AA")

  "Add property request" should "return the first name" in {
    val result = addPropertyRequest.firstName
    result shouldBe "John"
  }

  "Add property request" should "return the last name" in {
    val result = addPropertyRequest.lastName
    result shouldBe "Smith"
  }

  "Add property request" should "return the reference number" in {
    val result = addPropertyRequest.reference
    result shouldBe "1234567890"
  }

  "Add property request" should "return the postcode end part" in {
    val result = addPropertyRequest.postcodeEndPart
    result shouldBe "0AA"
  }
}
