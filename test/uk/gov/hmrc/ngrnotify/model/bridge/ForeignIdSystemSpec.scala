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

class ForeignIdSystemSpec extends AnyFreeSpec with Matchers {
  "ForeignIdSystem" - {
    "must have all expected values" in {
      val systems = ForeignIdSystem.values.toSet
      systems mustBe Set(ForeignIdSystem.Government_Gateway, ForeignIdSystem.Billing_Authority, ForeignIdSystem.Companies_House, ForeignIdSystem.National_Address_Gazetteer, ForeignIdSystem.NDRRPublicInterface, ForeignIdSystem.HMRC_VOA_CDB, ForeignIdSystem.SystemX)
    }

    "serialization" - {
      ForeignIdSystem.values foreach { system =>
        s"must serialize and deserialize ${system.toString} correctly" in {
          val serialized = Json.toJson(system)
          val deserialized = serialized.as[ForeignIdSystem]
          deserialized mustBe system
        }
      }
    }
  }
}
