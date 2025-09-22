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
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testMetaData
import uk.gov.hmrc.ngrnotify.model.response.bridge.MetaData

class MetaDataSpec extends AnyWordSpec with Matchers {
  private val metaDataJson = Json.obj(
    "sending" -> Json.obj(
      "extracting" -> Json.obj("selecting" -> Json.obj()),
      "transforming" -> Json.obj("recontextualising" -> Json.obj(), "filtering" -> Json.obj(), "supplementing" -> Json.obj()),
      "loading" -> Json.obj("assuring" -> Json.obj(), "readying" -> Json.obj(), "signing" -> Json.obj(), "encrypting" -> Json.obj(), "sending" -> Json.obj())
    ),
    "receiving" -> Json.obj(
      "transforming" -> Json.obj("recontextualising" -> Json.obj(), "dropping" -> Json.obj(), "restoring" -> Json.obj()),
      "storing" -> Json.obj("inserting" -> Json.obj()),
      "unloading" -> Json.obj("assuring" -> Json.obj(), "readying" -> Json.obj(), "verifying" -> Json.obj(), "decrypting" -> Json.obj(), "receiving" -> Json.obj())
    )
  )

  "MetaData" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(testMetaData)
      json shouldBe metaDataJson
    }

    "deserialize from JSON correctly" in {
      val json = metaDataJson
      val metaData = json.as[MetaData]
      metaData shouldBe testMetaData
    }

    "round-trip JSON serialization and deserialization" in {
      val original = metaDataJson
      val json = Json.toJson(original)
      val parsed = json.as[MetaData]
      parsed shouldBe testMetaData
    }
  }
}

