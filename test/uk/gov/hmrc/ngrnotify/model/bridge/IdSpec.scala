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

package uk.gov.hmrc.ngrnotify.model.bridge

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe

class IdSpec extends AnyFreeSpec {
  
  "IdSpec" - {
    "serialization and deserialization of Id" in {
      import play.api.libs.json.Json
      import uk.gov.hmrc.ngrnotify.model.bridge.*

      val intId: Id = IntId(123)
      val stringId: Id = StringId("abc")
      val nullId: Id = NullId

      val intIdJson = Json.toJson(intId)
      val stringIdJson = Json.toJson(stringId)
      val nullIdJson = Json.toJson(nullId)

      intIdJson mustBe Json.parse("123")
      stringIdJson mustBe Json.parse("\"abc\"")
      nullIdJson mustBe Json.parse("null")

      val intIdFromJson = Json.fromJson[Id](intIdJson).get
      val stringIdFromJson = Json.fromJson[Id](stringIdJson).get
      val nullIdFromJson = Json.fromJson[Id](nullIdJson).get

      intIdFromJson mustBe intId
      stringIdFromJson mustBe stringId
      nullIdFromJson mustBe nullId
    }
  }

}
