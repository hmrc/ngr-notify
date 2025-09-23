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
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testLoading
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.{Encrypting, Loading, LoadingSending, Signing}

class LoadingSpec extends AnyWordSpec with Matchers {
  private val loadingJson: JsObject = Json.obj(
    "assuring" -> Json.obj(),
    "readying" -> Json.obj(),
    "signing" -> Json.obj(),
    "encrypting" -> Json.obj(),
    "sending" -> Json.obj())

  "Loading" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(testLoading)
      json shouldBe loadingJson
    }

    "deserialize from JSON correctly" in {
      val json = loadingJson
      val loading = json.as[Loading]
      loading shouldBe testLoading
    }

    "round-trip JSON serialization and deserialization" in {
      val original = loadingJson
      val json = Json.toJson(original)
      val parsed = json.as[Loading]
      parsed shouldBe testLoading
    }
  }

  "Signing" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Signing())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val signing = json.as[Signing]
      signing shouldBe Signing()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[Signing]
      parsed shouldBe Signing()
    }
  }

  "Encrypting" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(Encrypting())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val encrypting = json.as[Encrypting]
      encrypting shouldBe Encrypting()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[Encrypting]
      parsed shouldBe Encrypting()
    }
  }

  "LoadingSending" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(LoadingSending())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val loadingSending = json.as[LoadingSending]
      loadingSending shouldBe LoadingSending()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[LoadingSending]
      parsed shouldBe LoadingSending()
    }
  }
}
