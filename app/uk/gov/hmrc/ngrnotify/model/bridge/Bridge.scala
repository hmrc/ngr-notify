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

import play.api.libs.json.*
import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.ProductData

object Bridge:

  import play.api.libs.json.JsError
  import play.api.libs.json.JsResult
  import play.api.libs.json.JsSuccess
  import play.api.libs.json.JsValue
  import play.api.libs.json.Reads

  type Id = String

  given Reads[Id] = new Reads[Id] {
    def reads(jsValue: JsValue): JsResult[Id] =
      jsValue match
        case JsNumber(num) => JsSuccess(num.toString)
        case JsString(str) => JsSuccess(str)
        case _             => JsError("Expected a JSON number or string")
  }

  //
  //   NOTE
  //   -----
  //
  //   Note that the Bridge API engineers wanted to model the JSON `id` property as having
  //   either JSON null or JSON number (such as 123) or JSON string (such as "123") values.
  //
  //   Instead, we believe that the `id` JSON values should have been modeled as having either
  //   JSON null or JSON string value (such as "123"), simply because that would have included
  //   all practical scenarios of usage. Despite our belief, we might need to mirror the Bridge
  //   API design, and therefore employ Scala 3 union types.
  //
  //   We hope that the following alternative Scala code will not have to replace the above:
  //
  //      ┌──────────────────────────────────────────────────────────────────
  //      │ type Id = Int | String
  //      │
  //      │  given Reads[Id] = new Reads[Id] {
  //      │    def reads(jsValue: JsValue): JsResult[Id] =
  //      │      jsValue match
  //      │        case JsNumber(num) => JsSuccess(num.toInt)
  //      │        case JsString(str) => JsSuccess(str)
  //      │        case  _            => JsError("Expected an Int or String")
  //      │  }
  //      │
  //      │  given Writes[Id] = new Writes[Id] {
  //      │    override def writes(intOrString: Id): JsValue =
  //      │      intOrString match
  //      │        case int: Int => JsNumber(int)
  //      │        case str: String => JsString(str)
  //      │  }
  //      └──────────────────────────────────────────────────────────────────
  //
  //

  // ========================================

  // TODO Improve the way we're defining EmptyItems (as it should be something better than List[String])
  type ItemEntity = String
  type EmptyItems = List[ItemEntity]

  // ========================================

  type ProductItem = PersonEntity | PropertyEntity // TODO | RelationshipEntity

  given Reads[ProductItem] = new Reads[ProductItem] {
    def reads(jsValue: JsValue): JsResult[ProductItem] =
      jsValue.validate[PersonEntity]
        .orElse(jsValue.validate[PropertyEntity])
      // TODO .orElse(jsValue.validate[RelationshipEntity])
  }

  given Writes[ProductItem] = new Writes[ProductItem] {
    def writes(productItem: ProductItem): JsValue = productItem match {
      case personEntity: PersonEntity     => Json.toJson(personEntity)
      case propertyEntity: PropertyEntity => Json.toJson(propertyEntity)
    }
  }

  // ========================================

  type ProductData = PersonData | PropertyData | RelationshipData

  given Reads[ProductData] = new Reads[ProductData] {
    def reads(jsValue: JsValue): JsResult[ProductData] =
      //
      // The best solution could have been reading the ..\category\code (by traversing up to the parent node)
      // and then reading the curren data node as either Person, or Property, or Relationship depending on
      // the category code, which can be either TX-DOM-PSN, TX-DOM-PRP or TX-DOM-REL.
      //
      // However, since the PlayFramework JSON library does not support such a "traversing up" access strategy,
      // we have to resort on chaining multiple attempts until one succeeds.
      //
      jsValue.validate[PersonData]
        .orElse(jsValue.validate[PropertyData])
        .orElse(jsValue.validate[RelationshipData])
  }

  given Writes[ProductData] = new Writes[ProductData] {
    def writes(productData: ProductData): JsValue = productData match {
      case personData: PersonData             => Json.toJson(personData)
      case propertyData: PropertyData         => Json.toJson(propertyData)
      case relationshipData: RelationshipData => Json.toJson(relationshipData)
    }
  }
