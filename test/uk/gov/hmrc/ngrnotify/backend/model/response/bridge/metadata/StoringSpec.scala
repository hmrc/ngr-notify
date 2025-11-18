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

package uk.gov.hmrc.ngrnotify.backend.model.response.bridge.metadata

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testStoring
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.{Inserting, Storing}

class StoringSpec extends AnyWordSpec with Matchers {
  private val storingJson = Json.obj("inserting" -> Json.obj())

  "Storing" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testStoring)
      json shouldBe storingJson
    }

    "deserialize from JSON correctly" in {
      val json    = storingJson
      val storing = json.as[Storing]
      storing shouldBe testStoring
    }

    "round-trip JSON serialization and deserialization" in {
      val original = storingJson
      val json     = Json.toJson(original)
      val parsed   = json.as[Storing]
      parsed shouldBe testStoring
    }
  }

  "Inserting" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Inserting())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json      = Json.obj()
      val inserting = json.as[Inserting]
      inserting shouldBe Inserting()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json     = Json.toJson(original)
      val parsed   = json.as[Inserting]
      parsed shouldBe Inserting()
    }
  }
}
