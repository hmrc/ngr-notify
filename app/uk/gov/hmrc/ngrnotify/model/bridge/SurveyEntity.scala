/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.libs.json.{Format, Json, OWrites}
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

case class SurveyEntity(
  id: NullableValue[Id],
  idx: String,
  name: NullableValue[String],
  label: String,
  description: NullableValue[String],
  origination: NullableValue[String],
  termination: NullableValue[String],
  category: CodeMeaning,
  `type`: CodeMeaning,
  `class`: CodeMeaning,
  data: ContainerData,
  protodata: List[Protodata],
  metadata: Metadata,
  compartments: Option[Map[String, String]] = None,
  items: Option[List[SurveyEntity]] = None
) extends Entity[ContainerData, Option[Map[String, String]], Option[List[SurveyEntity]]]
  with StandardProperties

object SurveyEntity {

  given Format[SurveyEntity] = Json.format

  case class PhysicalDetails(
    description: String,
    quantity: BigDecimal,
    units: String
  )

  object PhysicalDetails:
    given OWrites[PhysicalDetails] = Json.writes

  case class ReviewDetails(floorsInfo: List[LevelSummary], otherAdditionInfo:List[LevelSummary], parkingInfo: List[LevelSummary], totalArea: BigDecimal, fullAddress: Option[String])

  object ReviewDetails:
    given OWrites[ReviewDetails] = Json.writes

  case class LevelSummary(
    label: String,
    spaces: List[PhysicalDetails],
    totalArea: BigDecimal
  )

  object LevelSummary:
    given OWrites[LevelSummary] = Json.writes

  private def getPhysicalDetails(ed: ED): Seq[PhysicalDetails] =
    (for {
      desc  <- ed.description.value.value
      units <- ed.units.value.value
      qty   <- ed.quantity.value.value
    } yield PhysicalDetails(desc, qty, units)).toList

  private def getSpaces(e: SurveyEntity): List[PhysicalDetails] =
    e.items.getOrElse(Nil)
      .filter(x => x.`type`.code == "HOR" && (x.`class`.code == "BUV" || x.`class`.code == "PUV" || x.`class`.code == "AUV"))
      .flatMap(_.data.uses.flatMap(getPhysicalDetails))

  def extractFloorAndParkingData(entity: SurveyEntity, fullAddress: Option[String]): ReviewDetails = {
    def recurse(e: SurveyEntity): ReviewDetails = {
      val childDetails: Seq[ReviewDetails] = e.items.getOrElse(Nil).map(recurse)
      val isVerticalType = e.`type`.code == "VER"
      val isFloor                          = isVerticalType && e.`class`.code == "BLV"
      val isParking                        = isVerticalType && e.`class`.code == "PLV"
      val isAdditionalUnits                = isVerticalType && e.`class`.code == "ALV"

      val spaces      = getSpaces(e)
      val currentArea = spaces.map(_.quantity).sum

      val floors  = (if (isFloor) List(LevelSummary(e.label, spaces, currentArea)) else Nil) ++ childDetails.flatMap(_.floorsInfo)
      val parking = (if (isParking) List(LevelSummary(e.label, spaces, currentArea)) else Nil) ++ childDetails.flatMap(_.parkingInfo)
      val additionalUnits = (if (isAdditionalUnits) List(LevelSummary(e.label, spaces, currentArea)) else Nil) ++ childDetails.flatMap(_.otherAdditionInfo)
      val total   = currentArea + childDetails.flatMap(_.floorsInfo).map(_.totalArea).sum

      ReviewDetails(floors, additionalUnits, parking, total, fullAddress)
    }

    recurse(entity)
  }
}
