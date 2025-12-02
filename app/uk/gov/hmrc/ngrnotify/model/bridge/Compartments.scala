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

import play.api.libs.json.Format
import play.api.libs.json.*

case class Compartments(
  properties: List[PropertyEntity] = List.empty,
  persons: List[PersonEntity] = List.empty,
  processes: List[JsValue] = List.empty,
  products: List[ProductEntity] = List.empty,
  relationships: List[JsValue] = List.empty
)

object Compartments {

  given Format[Compartments] = new Format[Compartments] {
    override def writes(c: Compartments): JsValue =
      if (
        c.properties.isEmpty &&
        c.persons.isEmpty &&
        c.processes.isEmpty &&
        c.products.isEmpty &&
        c.relationships.isEmpty
      ) JsObject.empty
      else Json.obj(
        "properties"    -> c.properties,
        "persons"       -> c.persons,
        "processes"     -> c.processes,
        "products"      -> c.products,
        "relationships" -> c.relationships
      )

    override def reads(json: JsValue): JsResult[Compartments] = {
      def getList[T: Format](field: String): List[T] =
        (json \ field).validate[List[T]] match {
          case JsSuccess(value, _) => value
          case JsError(errors)     =>
            println(s"JSON error for field '$field': " + errors)
            Nil
        }

      json match {
        case JsObject(fields) if fields.isEmpty => JsSuccess(Compartments())
        case x                                  =>
          JsSuccess(
            Compartments(
              getList[PropertyEntity]("properties"),
              getList[PersonEntity]("persons"),
              getList[JsValue]("processes"),
              getList[ProductEntity]("products"),
              getList[JsValue]("relationships")
            )
          )
      }
    }
  }
}
