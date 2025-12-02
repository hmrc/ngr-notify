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
import play.api.libs.json.{JsNull, JsNumber, JsString}

class IdSpec extends AnyFreeSpec {

  "IdSpec" - {
    "serialization and deserialization of Id" in {
      import play.api.libs.json.Json
      import uk.gov.hmrc.ngrnotify.model.bridge.*

      val intId: Id    = IntId(123)
      val stringId: Id = StringId("abc")
      val nullId: Id   = NullId

      Json.toJson(intId) mustBe JsNumber(123)
      Json.toJson(stringId) mustBe JsString("abc")
      Json.toJson(nullId) mustBe JsNull

      JsNumber(123).as[Id] mustBe IntId(123)
      JsString("abc").as[Id] mustBe StringId("abc")
      JsNull.as[Id] mustBe NullId
    }
  }

}
