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

import play.api.libs.json.{JsNull, JsSuccess, Reads}


sealed trait Compartments

case class CompartmentEntity(
  properties: List[PropertyEntity] = List.empty,
  persons: List[PersonEntity] = List.empty,
  // TODO processes: List[ProcessEntity] = List.empty,
  relationships: List[RelationshipEntity] = List.empty,
  products: List[ProductEntity] = List.empty
) extends Compartments

case object NullCompartments extends Compartments
case object EmptyCompartmentsEntity extends Compartments

object Compartments {
  import play.api.libs.json.*

  implicit val compartmentEntityFormat: OFormat[CompartmentEntity] = Json.format[CompartmentEntity]
  implicit val compartmentsReads: Reads[Compartments] = Reads[Compartments] {
    case JsNull => JsSuccess(NullCompartments)
    case json if json.asOpt[CompartmentEntity](using compartmentEntityFormat).isEmpty =>
      JsSuccess(EmptyCompartmentsEntity)
    case json =>
      json.validate[CompartmentEntity](using compartmentEntityFormat).map(data => if(isEmpty(data)) EmptyCompartmentsEntity else data)
  }

  implicit val compartmentsWrites: Writes[Compartments] = Writes[Compartments] {
    case NullCompartments    => JsNull
    case EmptyCompartmentsEntity   => JsObject.empty
    case e: CompartmentEntity => compartmentEntityFormat.writes(e)
  }
  
  def isEmpty(compartments: Compartments): Boolean = {
    compartments match {
      case NullCompartments => true
      case EmptyCompartmentsEntity => true
      case CompartmentEntity(props, persons, relationships, products) =>
        props.isEmpty && persons.isEmpty && relationships.isEmpty && products.isEmpty
    }
  }
    
    def products(compartments: Compartments): Seq[ProductEntity] = {
    compartments match {
      case CompartmentEntity(_, _, _, products) => products
      case _                                    => List.empty
    } 
  }
    
    def persons(compartments: Compartments): Seq[PersonEntity] = {
      compartments match {
        case CompartmentEntity(_, persons, _, _) => persons
        case _ => List.empty
      }
    } 
    
    def properties(compartments: Compartments): Seq[PropertyEntity] = {
      compartments match {
        case CompartmentEntity(properties, _, _, _) => properties
        case _ => List.empty
      }
    }
    
    def relationships(compartments: Compartments): Seq[RelationshipEntity] = {
      compartments match {
        case CompartmentEntity(_, _, relationships, _) => relationships
        case _ => List.empty
      }
    }
}
