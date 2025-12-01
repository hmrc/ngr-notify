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
import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.libs.json.{JsNull, JsSuccess, Json}

class CompartmentsSpec extends AnyFreeSpec {
  
  "Compartments" - {
    "serialise and deserialise CompartmentEntity correctly" in {
      val entity = CompartmentEntity(
        properties = List.empty,
        persons = List.empty,
        relationships = List.empty,
        products = List.empty
      )
      val json = Json.toJson(entity: Compartments)
      json.validate[Compartments] shouldBe JsSuccess(EmptyCompartmentsEntity)
    }

    "serialise and deserialise CompartmentEntity with data correctly" in {
      val metadata: Metadata = Metadata(Sending(Extracting(), Transforming(), Loading()), Receiving(Unloading(), TransformingReceiving(), Storing()))

      val samplePropertyEntity = PropertyEntity(
        id = Some(StringId("id1")),
        idx = "idx1",
        name = Some("Sample Name"),
        label = "Sample Label",
        description = Some("Sample Description"),
        origination = Some("Origin"),
        termination = Some("Termination"),
        protodata = List.empty,
        metadata = metadata,
        category = CodeMeaning("cat", Some("Category")),
        `type` = CodeMeaning("type", Some("Type")),
        `class` = CodeMeaning("class", Some("Class")),
        data = PropertyData(),
        compartments = EmptyCompartmentsEntity,
        items = List("item1", "item2")
      )

      val entity = CompartmentEntity(
        properties = List(samplePropertyEntity),
        persons = List.empty,
        relationships = List.empty,
        products = List.empty
      )
      val json = Json.toJson(entity: Compartments)
      json.validate[Compartments] shouldBe JsSuccess(CompartmentEntity(List(PropertyEntity(Some(StringId("id1")), "idx1", Some("Sample Name"), "Sample Label", Some("Sample Description"), Some("Origin"), Some("Termination"), List(), Metadata(Sending(Extracting(Map()), Transforming(Map(), Map(), Map()), Loading(Map(), Map(), Signing(None), Map(), Map())), Receiving(Unloading(Map(), Map(), Map(), Map(), Map()), TransformingReceiving(Map(), Map(), Map()), Storing(Map()))), CodeMeaning("cat", Some("Category")), CodeMeaning("type", Some("Type")), CodeMeaning("class", Some("Class")), PropertyData(List(), List(), List(), PropertyAddresses(None, None, None, None), None, List()), EmptyCompartmentsEntity, List("item1", "item2"))), List(), List(), List()))

    }

    "serialise and deserialise NullCompartments correctly" in {
      val json = Json.toJson(NullCompartments: Compartments)
      json shouldBe JsNull
      json.validate[Compartments] shouldBe JsSuccess(NullCompartments)
    }

    "serialise and deserialise EmptyCompartmentsEntity correctly" in {
      val json = Json.toJson(EmptyCompartmentsEntity: Compartments)
      json.validate[Compartments] shouldBe JsSuccess(EmptyCompartmentsEntity)
    }
  }

}
