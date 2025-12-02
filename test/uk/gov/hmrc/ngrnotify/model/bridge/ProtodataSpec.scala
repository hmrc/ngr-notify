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

class ProtodataSpec extends AnyFreeSpec {
  "serialization and deserialization of ProtoData" in {
    val expectedModel = Protodata(
      node = Some("string"),
      mime_type = "image/jpeg",
      label = "string",
      is_pointer = Some(true),
      pointer = "string",
      data = ""
    )
    val json          = Json.parse("""
                                     |{
                                     |        "node": "string",
                                     |        "mime_type": "image/jpeg",
                                     |        "label": "string",
                                     |        "is_pointer": true,
                                     |        "pointer": "string",
                                     |        "data": ""
                                     |      }
                                     |""".stripMargin)

    json.as[Protodata] mustBe expectedModel
    Json.toJson(expectedModel) mustBe json
  }
}
