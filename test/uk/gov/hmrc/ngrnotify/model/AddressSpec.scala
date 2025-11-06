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

package uk.gov.hmrc.ngrnotify.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

/**
  * @author Yuriy Tumakha
  */
class AddressSpec extends AnyWordSpec with Matchers:

  private val normalizedSingleLine = "Line 1, Line 2, City, ZZ11 1ZZ"

  "Model Address" should {
    "be serialized/deserialized from JSON" in {
      val address = Address("Line 1", Some("Line 2"), "City", None, Postcode("ZZ11 1ZZ"))

      val convertedAddress = Json.toJson(address).as[Address]
      convertedAddress            shouldBe address
      convertedAddress.singleLine shouldBe normalizedSingleLine
    }

    "be serialized/deserialized from JSON and postcode reformatted in .singleLine" in {
      val address = Address("Line 1", Some("Line 2"), "City", None, Postcode("ZZ11   1ZZ"))

      val convertedAddress = Json.toJson(address).as[Address]
      convertedAddress            shouldBe address
      convertedAddress.singleLine shouldBe normalizedSingleLine
    }
  }
