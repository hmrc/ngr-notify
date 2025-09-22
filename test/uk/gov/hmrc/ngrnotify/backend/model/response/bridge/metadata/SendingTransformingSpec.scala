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
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testSendingTransforming
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.{Filtering, SendingTransforming, Supplementing}

class SendingTransformingSpec extends AnyWordSpec with Matchers {
  private val sendingTransformingJson = Json.obj(
    "recontextualising" -> Json.obj(),
    "filtering" -> Json.obj(),
    "supplementing" -> Json.obj())

  "SendingTransforming" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testSendingTransforming)
      json shouldBe sendingTransformingJson
    }

    "deserialize from JSON correctly" in {
      val json = sendingTransformingJson
      val sendingTransforming = json.as[SendingTransforming]
      sendingTransforming shouldBe testSendingTransforming
    }

    "round-trip JSON serialization and deserialization" in {
      val original = sendingTransformingJson
      val json = Json.toJson(original)
      val parsed = json.as[SendingTransforming]
      parsed shouldBe testSendingTransforming
    }
  }

  "Filtering" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Filtering())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val assuring = json.as[Filtering]
      assuring shouldBe Filtering()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[Filtering]
      parsed shouldBe Filtering()
    }
  }

  "Supplementing" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(Supplementing())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj()
      val assuring = json.as[Supplementing]
      assuring shouldBe Supplementing()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json = Json.toJson(original)
      val parsed = json.as[Supplementing]
      parsed shouldBe Supplementing()
    }
  }
}

