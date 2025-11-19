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
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.{Extracting, Selecting}

class ExtractingSpec extends AnyWordSpec with Matchers {

  "Extracting" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Extracting(Selecting()))
      json shouldBe Json.obj("selecting" -> Json.obj())
    }

    "deserialize from JSON correctly" in {
      val json       = Json.obj("selecting" -> Json.obj())
      val extracting = json.as[Extracting]
      extracting shouldBe Extracting(Selecting())
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj("selecting" -> Json.obj())
      val json     = Json.toJson(original)
      val parsed   = json.as[Extracting]
      parsed shouldBe Extracting(Selecting())
    }
  }

  "Selecting" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(Selecting())
      json shouldBe Json.obj()
    }

    "deserialize from JSON correctly" in {
      val json      = Json.obj()
      val selecting = json.as[Selecting]
      selecting shouldBe Selecting()
    }

    "round-trip JSON serialization and deserialization" in {
      val original = Json.obj()
      val json     = Json.toJson(original)
      val parsed   = json.as[Selecting]
      parsed shouldBe Selecting()
    }
  }
}
