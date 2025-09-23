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
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testReceiving
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.Receiving

class ReceivingSpec extends AnyWordSpec with Matchers {
  private val receivingJson = Json.obj(
    "transforming" -> Json.obj("recontextualising" -> Json.obj(), "dropping" -> Json.obj(), "restoring" -> Json.obj()),
    "storing" -> Json.obj("inserting" -> Json.obj()),
    "unloading" -> Json.obj("assuring" -> Json.obj(), "readying" -> Json.obj(), "verifying" -> Json.obj(), "decrypting" -> Json.obj(), "receiving" -> Json.obj())
  )

  "Receiving" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testReceiving)
      json shouldBe receivingJson
    }

    "deserialize from JSON correctly" in {
      val json = receivingJson
      val receiving = json.as[Receiving]
      receiving shouldBe testReceiving
    }

    "round-trip JSON serialization and deserialization" in {
      val original = receivingJson
      val json = Json.toJson(original)
      val parsed = json.as[Receiving]
      parsed shouldBe testReceiving
    }
  }
}
