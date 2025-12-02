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
import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.WildcardType
//import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.ProductData

object Bridge:

  import play.api.libs.json.{JsResult, JsValue, Reads}

  /*  type Id = String

  given Reads[Id] = new Reads[Id] {
    def reads(jsValue: JsValue): JsResult[Id] =
      jsValue match
        case JsNumber(num) => JsSuccess(num.toString)
        case JsString(str) => JsSuccess(str)
        case _             => JsError("Expected a JSON number or string")
  }

  given Writes[Id] = new Writes[Id] {
    override def writes(id: Id): JsValue = JsString(id)
  }*/

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

  type ProductItem      = PersonEntity | PropertyEntity | RelationshipItem
  type RelationshipItem = Pointer

  given Reads[ProductItem] = new Reads[ProductItem] {
    def reads(jsValue: JsValue): JsResult[ProductItem] =
      jsValue.validate[PersonEntity]
        .orElse(
          jsValue.validate[PropertyEntity]
            .orElse(jsValue.validate[RelationshipItem])
        )
  }

  given Writes[ProductItem] = new Writes[ProductItem] {
    def writes(productItem: ProductItem): JsValue = productItem match {
      case personEntity: PersonEntity         => Json.toJson(personEntity)
      case propertyEntity: PropertyEntity     => Json.toJson(propertyEntity)
      case relationshipItem: RelationshipItem => Json.toJson(relationshipItem)
    }
  }

  // ========================================

  type ProductData = PersonData | PropertyData | RelationshipData

  given Reads[ProductData] = new Reads[ProductData] {
    def reads(jsValue: JsValue): JsResult[ProductData] = {
      // Try to extract the category code from the parent object if available
      val categoryCodeOpt = (jsValue \ ".." \ "category" \ "code").asOpt[String]

      categoryCodeOpt match {
        case Some("TX-DOM-PSN") => jsValue.validate[PersonData]
        case Some("TX-DOM-PRP") => jsValue.validate[PropertyData]
        case Some("TX-DOM-REL") => jsValue.validate[RelationshipData]
        case _ =>
          // Fallback: try all types in order
          jsValue.validate[PersonData]
            .orElse(jsValue.validate[PropertyData])
            .orElse(jsValue.validate[RelationshipData])
      }
    }
  }

  given Writes[ProductData] = new Writes[ProductData] {
    def writes(productData: ProductData): JsValue = productData match {
      case personData: PersonData             => Json.toJson(personData)
      case propertyData: PropertyData         => Json.toJson(propertyData)
      case relationshipData: RelationshipData => Json.toJson(relationshipData)
    }
  }

  /**
    * This is the Scala type to adopt for those properties defined as of type `{}` (wildcard type)
    * by the Bridge API JSON Schema. A property of type wildcard means the JSON value can be any of
    * the 6 JSON alternatives (which are: string, number, boolean, null, object, array).
    *
    * WildcardType is just a synonym for the PlayFramework JsValue type. Its adoption is discouraged,
    * as it would require more and more Scala code to handle the various JSON alternatives. Instead,
    * it is recommended to persuade the Bridge API engineers to model the JSON properties as having
    * a more specific type, such as JSON string, number, or empty object instead.
    */
  type WildcardType = JsValue

given Reads[WildcardType] = new Reads[WildcardType] {
  def reads(jsValue: JsValue): JsResult[WildcardType] = jsValue match {
    case JsNull                             => JsSuccess(JsNull)
    case JsObject(fields) if fields.isEmpty => JsSuccess(JsObject.empty) // handle {}
    case obj: JsObject                      => JsSuccess(obj)
    case _                                  => JsError("Expected JsNull or JsObject")
  }
}

given Writes[WildcardType] = new Writes[WildcardType] {
  def writes(value: WildcardType): JsValue = value match {
    case JsNull                             => JsNull
    case JsObject(fields) if fields.isEmpty => JsObject.empty // handle {}
    case obj: JsObject                      => obj
    case _                                  => throw new IllegalArgumentException("Unreachable code: WildcardType should only be JsNull or JsObject")
  }
}
