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

class CompartmentsSpec extends AnyFreeSpec {
  "serialization and deserialization of Compartments" in {
    val json = Json.parse(
      """
        |  {
        |    "properties": [],
        |    "persons": [],
        |    "relationships": [],
        |    "products": []
        |  }
        |""".stripMargin)
    val compartments = json.as[Compartments]
    val expectedCompartments = Compartments(
      properties = List.empty,
      persons = List.empty,
      relationships = List.empty,
      products = List.empty
    )
    compartments mustBe expectedCompartments
  }

  "empty compartments should serialize to empty JSON object" in {
    val compartments = Compartments(
      properties = List.empty,
      persons = List.empty,
      relationships = List.empty,
      products = List.empty
    )
    val json = Json.toJson(compartments)
    json mustBe Json.obj()
  }
}
