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
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testReceivingTransforming
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.{Dropping, ReceivingTransforming, Restoring}

class ReceivingTransformingSpec extends AnyWordSpec with Matchers {

  private val receivingTransformingJson = Json.obj(
    "recontextualising" -> Json.obj(),
    "dropping"          -> Json.obj(),
    "restoring"         -> Json.obj()
  )

  "ReceivingTransforming" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testReceivingTransforming)
      json shouldBe receivingTransformingJson
    }

    "deserialize from JSON correctly" in {
      val json                  = receivingTransformingJson
      val receivingTransforming = json.as[ReceivingTransforming]
      receivingTransforming shouldBe testReceivingTransforming
    }

    "round-trip JSON serialization and deserialization" in {
      val original = receivingTransformingJson
      val json     = Json.toJson(original)
      val parsed   = json.as[ReceivingTransforming]
      parsed shouldBe testReceivingTransforming
    }
  }

  "Dropping" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Dropping())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json     = Json.obj()
      val dropping = json.as[Dropping]
      dropping shouldBe Dropping()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json     = Json.toJson(original)
      val parsed   = json.as[Dropping]
      parsed shouldBe Dropping()
    }
  }

  "Restoring" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(Restoring())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json      = Json.obj()
      val restoring = json.as[Restoring]
      restoring shouldBe Restoring()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json     = Json.toJson(original)
      val parsed   = json.as[Restoring]
      parsed shouldBe Restoring()
    }
  }
}
