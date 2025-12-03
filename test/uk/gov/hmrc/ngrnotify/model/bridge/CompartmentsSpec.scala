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
import play.api.libs.json.{JsNull, JsObject, Json}
import uk.gov.hmrc.ngrnotify.model.bridge
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.Government_Gateway
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

class CompartmentsSpec extends AnyFreeSpec with Data {
  "serialization and deserialization of Compartments" in {
    val json                 = Json.parse(
      """
        |  {
        |    "properties": [],
        |    "persons": [],
        |    "processes": [],
        |    "relationships": [],
        |    "products": []
        |  }
        |""".stripMargin
    )
    val compartments         = json.as[Compartments]
    val expectedCompartments = Compartments(
      properties = List.empty,
      persons = List.empty,
      processes = List.empty,
      relationships = List.empty,
      products = List.empty
    )
    compartments mustBe expectedCompartments
  }

  "empty compartments should serialize to empty JSON object" in {
    val compartments = Compartments(
      properties = List.empty,
      persons = List.empty,
      processes = List.empty,
      relationships = List.empty,
      products = List.empty
    )
    val json         = Json.toJson(compartments)
    json mustBe Json.obj()
  }

  "non-empty compartments should serialize correctly" in {
    val compartments = Compartments(
      properties = List(samplePropertyEntity()),
      persons = List.empty,
      processes = List.empty,
      relationships = List.empty,
      products = List.empty
    )
    val json         = Json.toJson(compartments)
    val expectedJson = Json.parse(
      """
        |{
        |  "properties": [
        |    {
        |      "id": "123",
        |      "idx": "P001",
        |      "name": "Sample Product",
        |      "label": "Sample Label",
        |      "description": "A sample product for testing.",
        |      "origination": "Origin",
        |      "termination": "Termination",
        |      "category": {
        |        "code": "LTX-DOM-PRP",
        |        "meaning": "Category 1"
        |      },
        |      "type": {
        |        "code": "TYPE001",
        |        "meaning": "Type 1"
        |      },
        |      "class": {
        |        "code": "CLASS001",
        |        "meaning": "Class 1"
        |      },
        |      "data": {
        |        "foreign_ids": [
        |          {
        |            "system": "Government_Gateway",
        |            "location": "location",
        |            "value": "SomeId"
        |          }
        |        ],
        |        "foreign_names": [],
        |        "foreign_labels": [],
        |        "addresses": {
        |          "property_full_address": null,
        |          "address_line_1": null,
        |          "address_postcode": null,
        |          "address_known_as": null
        |        },
        |        "location": {
        |          "local_authority_pseudo_area_code": null,
        |          "ordanace_survey_coordinates": null,
        |          "google_maps_coordinates": null
        |        },
        |        "assessments": []
        |      },
        |      "protodata": [],
        |      "metadata": {
        |        "sending": {
        |          "extracting": {
        |            "selecting": null
        |          },
        |          "transforming": {
        |            "filtering": null,
        |            "supplementing": null,
        |            "recontextualising": null
        |          },
        |          "loading": {
        |            "readying": null,
        |            "assuring": null,
        |            "signing": {"inputs":{"hash":"str","signature":"str"}},
        |            "encrypting": null,
        |            "sending": null
        |          }
        |        },
        |        "receiving": {
        |          "unloading": {
        |            "receiving": null,
        |            "decrypting": null,
        |            "verifying": null,
        |            "assuring": null,
        |            "readying": null
        |          },
        |          "transforming": {
        |            "recontextualising": null,
        |            "dropping": null,
        |            "restoring": null
        |          },
        |          "storing": {
        |            "inserting": null
        |          }
        |        }
        |      },
        |      "compartments": {},
        |      "items": []
        |    }
        |  ],
        |  "persons": [],
        |  "processes":[],
        |  "products": [],
        |  "relationships": []
        |}
        |
        |""".stripMargin
    )
    json mustBe expectedJson
  }
}

trait Data {

  val metadata: bridge.Metadata = bridge.Metadata(
    Sending(
      Extracting(JsNull),
      Transforming(JsNull, JsNull, JsNull),
      Loading(
        JsNull,
        JsNull,
        Json.obj(
          "inputs" -> Json.obj(
            "hash"      -> "str",
            "signature" -> "str"
          )
        ),
        JsNull,
        JsNull
      )
    ),
    Receiving(Unloading(JsNull, JsNull, JsNull, JsNull, JsNull), TransformingReceiving(JsNull, JsNull, JsNull), Storing(JsNull))
  )

  val propertyData = PropertyData(List(ForeignDatum(Some(Government_Gateway), Some("location"), Some("SomeId"))), List.empty, List.empty, PropertyAddresses())

  def samplePropertyEntity(categoryCode: String = "LTX-DOM-PRP") = PropertyEntity(
    id = NullableValue(Some(StringId("123"))),
    idx = "P001",
    name = NullableValue(Some("Sample Product")),
    label = "Sample Label",
    description = NullableValue(Some("A sample product for testing.")),
    origination = NullableValue(Some("Origin")),
    termination = NullableValue(Some("Termination")),
    protodata = List.empty,
    metadata = metadata,
    category = CodeMeaning(categoryCode, NullableValue(Some("Category 1"))),
    `type` = CodeMeaning("TYPE001", NullableValue(Some("Type 1"))),
    `class` = CodeMeaning("CLASS001", NullableValue(Some("Class 1"))),
    data = propertyData,
    compartments = Compartments(),
    items = List.empty
  )
}
