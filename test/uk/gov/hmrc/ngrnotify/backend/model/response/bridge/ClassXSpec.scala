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
import play.api.libs.json._
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testClassX
import uk.gov.hmrc.ngrnotify.model.response.bridge.ClassX

class ClassXSpec extends AnyWordSpec with Matchers {

  private val classXJson = Json.obj(
    "code"    -> "TestClassXCode",
    "meaning" -> "TestClassXMeaning"
  )

  "ClassX" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(testClassX)
      json shouldBe classXJson
    }

    "deserialize from JSON correctly" in {
      val json   = classXJson
      val classX = json.as[ClassX]
      classX shouldBe testClassX
    }

    "round-trip JSON serialization and deserialization" in {
      val original = classXJson
      val json     = Json.toJson(original)
      val parsed   = json.as[ClassX]
      parsed shouldBe testClassX
    }
  }
}
