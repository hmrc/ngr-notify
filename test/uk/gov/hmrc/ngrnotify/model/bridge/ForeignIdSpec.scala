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

class ForeignIdSpec extends AnyFreeSpec {
  "ForeignId" - {
    "serialize and deserialize correctly" in {
      import play.api.libs.json.Json

      val foreignId = ForeignId(
        system = Some(System.GovernmentGateway),
        location = Some(".path"),
        value = Some("SomeValue")
      )

      val json                = Json.toJson(foreignId)
      val deserializedForeignId = json.as[ForeignId]
      deserializedForeignId mustBe foreignId
    }
  }
}
