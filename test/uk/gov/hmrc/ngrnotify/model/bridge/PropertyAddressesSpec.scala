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

package uk.gov.hmrc.ngrnotify.model.bridge

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe

class PropertyAddressesSpec extends AnyFreeSpec {
  "PropertyAddresses JSON format" - {
    import play.api.libs.json.Json

    "should serialize and deserialize correctly" in {
      val address = PropertyAddresses(
        propertyFullAddress = Some("123 Main St, Anytown, AT1 2BC"),
        addressLine_1 = Some("123 Main St"),
        addressPostcode = Some("AT1 2BC"),
        addressKnownAs = Some("Home")
      )

      val json                = Json.toJson(address)
      val deserializedAddress = json.as[PropertyAddresses]
      deserializedAddress mustBe address
    }

    "serialize and deserialize with missing fields" in {
      val address = PropertyAddresses(
        propertyFullAddress = None,
        addressLine_1 = Some("123 Main St"),
        addressPostcode = None,
        addressKnownAs = Some("Home")
      )

      val expectedJson = Json.parse("""
                                      |{
                                      |  "property_full_address": null,
                                      |  "address_line_1": "123 Main St",
                                      |  "address_postcode": null,
                                      |  "address_known_as": "Home"
                                      |}
                                      |""".stripMargin)

      val json = Json.toJson(address)
      json mustBe expectedJson
    }
  }
}
