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
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testUnloading
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.{Decrypting, Unloading, UnloadingReceiving, Verifying}

class UnloadingSpec extends AnyWordSpec with Matchers {
  private val unloadingJson = Json.obj(
    "assuring" -> Json.obj(),
    "readying" -> Json.obj(),
    "verifying" -> Json.obj(),
    "decrypting" -> Json.obj(),
    "receiving" -> Json.obj())

  "Unloading" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testUnloading)
      json shouldBe unloadingJson
    }

    "deserialize from JSON correctly" in {
      val json = unloadingJson
      val unloading = json.as[Unloading]
      unloading shouldBe testUnloading
    }

    "round-trip JSON serialization and deserialization" in {
      val original = unloadingJson
      val json = Json.toJson(original)
      val parsed = json.as[Unloading]
      parsed shouldBe testUnloading
    }
  }

  "Verifying" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Verifying())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val verifying = json.as[Verifying]
      verifying shouldBe Verifying()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[Verifying]
      parsed shouldBe Verifying()
    }
  }

  "Decrypting" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(Decrypting())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val decrypting = json.as[Decrypting]
      decrypting shouldBe Decrypting()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[Decrypting]
      parsed shouldBe Decrypting()
    }
  }

  "Unloading Receiving" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(UnloadingReceiving())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val unloadingReceiving = json.as[UnloadingReceiving]
      unloadingReceiving shouldBe UnloadingReceiving()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[UnloadingReceiving]
      parsed shouldBe UnloadingReceiving()
    }
  }
}

