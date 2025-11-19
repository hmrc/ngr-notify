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

package uk.gov.hmrc.ngrnotify.backend.model.response.bridge

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testData
import uk.gov.hmrc.ngrnotify.model.response.bridge.Data

class DataSpec extends AnyWordSpec with Matchers {

  private val dataJson = Json.obj(
    "foreign_ids"    -> Json.arr("1", "2", "3"),
    "foreign_names"  -> Json.arr("Bob", "Brian", "Bill"),
    "foreign_labels" -> Json.arr("Label1", "Label2", "Label3")
  )

  "Data" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testData)
      json shouldBe dataJson
    }

    "deserialize from JSON correctly" in {
      val json = dataJson
      val data = json.as[Data]
      data shouldBe testData
    }

    "round-trip JSON serialization and deserialization" in {
      val original = dataJson
      val json     = Json.toJson(original)
      val parsed   = json.as[Data]
      parsed shouldBe testData
    }
  }
}
