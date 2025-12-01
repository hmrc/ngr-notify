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
import play.api.libs.json.Json

class PropertyEntitySpec extends AnyFreeSpec {

  "PropertyEntitySpec" - {
    "serialization and deserialization of PropertyEntity" in {
      val json         = Json.parse(testResourceContent("product-property-data.json"))

      val propertyEntity = json.as[PropertyEntity]
      val serialized = Json.toJson(propertyEntity)

      println("================= Serialized PropertyEntity ====1==========="+Json.prettyPrint(serialized))
      println("================= Serialized PropertyEntity =====2=========="+Json.prettyPrint(json))
      Json.prettyPrint(serialized) mustBe Json.prettyPrint(json)
    }
  }


}
