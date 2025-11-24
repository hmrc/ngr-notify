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
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.model.bridge.System.{BillingAuthority, CompaniesHouse, GovernmentGateway, HmrcVoaCdb, NationalAddressGazetteer}

class ForeignIdSystemSpec extends AnyFreeSpec with Matchers {
  "ForeignIdSystem" - {
    "must have all expected values" in {
      val systems = System.values
      systems mustBe Set(
        HmrcVoaCdb,
        NationalAddressGazetteer,
        System.GovernmentGateway,
        System.BillingAuthority,
        System.CompaniesHouse
      )
    }

     s"must serialize to JSON string" in {
        import play.api.libs.json.JsString
        Json.toJson[System](System.GovernmentGateway) mustBe JsString("Government_Gateway")
        Json.toJson[System](System.CompaniesHouse) mustBe JsString("Companies_House")
        Json.toJson[System](System.HmrcVoaCdb) mustBe JsString("HMRC-VOA_CDB")
        Json.toJson[System](System.BillingAuthority) mustBe JsString("Billing_Authority")
        Json.toJson[System](System.NationalAddressGazetteer) mustBe JsString("National_Address_Gazetteer")
      }

    "must deserialize from JSON string" in {
      Json.parse("\"Government_Gateway\"").as[System] mustBe GovernmentGateway
      Json.parse("\"HMRC-VOA_CDB\"").as[System] mustBe HmrcVoaCdb
      Json.parse("\"National_Address_Gazetteer\"").as[System] mustBe NationalAddressGazetteer
      Json.parse("\"Companies_House\"").as[System] mustBe CompaniesHouse
      Json.parse("\"Billing_Authority\"").as[System] mustBe BillingAuthority
    }
  }
}
