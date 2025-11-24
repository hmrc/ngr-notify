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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.{JobItem, Names}

sealed trait Data

case class PersonEntityData(
                             foreign_ids: Seq[ForeignId] = Seq.empty,
                             foreign_names: Seq[ForeignId] = Seq.empty,
                             foreign_labels: Seq[ForeignId] = Seq.empty,
                             names: Option[Names] = None,
                             communications: Option[Communications] = None
                           ) extends Data

case class PropertyEntityData(
                               foreign_ids: Seq[ForeignId] = Seq.empty,
                               foreign_names: Seq[ForeignId] = Seq.empty,
                               foreign_labels: Seq[ForeignId] = Seq.empty,
                               addresses: Option[PropertyAddresses] = None,
                               location: Option[Location] = None,
                               assessments: Option[Seq[JobItem]] = None
                             ) extends Data

object Data {
implicit val personEntityDataFormat: OFormat[PersonEntityData] = Json.format[PersonEntityData]
implicit val propertyEntityDataFormat: OFormat[PropertyEntityData] = Json.format[PropertyEntityData]
implicit val format: OFormat[Data] = {
  import play.api.libs.json.*
  OFormat(
    Reads[Data] { js =>
      (js \ "names").toOption match {
        case Some(_) => personEntityDataFormat.reads(js)
        case None    => propertyEntityDataFormat.reads(js)
      }
    },
    OWrites[Data] {
      case p: PersonEntityData   => personEntityDataFormat.writes(p)
      case p: PropertyEntityData => propertyEntityDataFormat.writes(p)
    }
  )
}
 
}


