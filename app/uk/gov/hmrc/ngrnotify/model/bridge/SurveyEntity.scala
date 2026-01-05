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

  case class Details(
    description: String,
    quantity: BigDecimal,
    units: String
  )

  object Details:
    given OWrites[Details] = Json.writes

  case class ReviewDetails(floorsInfo: List[LevelSummary], parkingInfo: List[LevelSummary], totalArea: BigDecimal)

  object ReviewDetails:
    given OWrites[ReviewDetails] = Json.writes

  case class LevelSummary(
                           label: String,
                           facilities: List[Details],
                           spaces: List[Details],
                           totalArea: BigDecimal
  )

  object LevelSummary:
    given OWrites[LevelSummary] = Json.writes

  def extractFloorAndParkingData(entity: SurveyEntity): ReviewDetails = {
    def getFacilities(e: SurveyEntity): List[Details] =
      val own: List[Details] =
        e.data.facilities.flatMap { x =>
          (for {
            desc  <- x.description.value.value
            units <- x.units.value.value
            qty   <- x.quantity.value.value
          } yield Details(desc, qty, units)).toList
        }

      val fromHor: List[Details] =
        e.items.getOrElse(Nil)
          .filter(_.`type`.code == "HOR")
          .flatMap(_.data.facilities.flatMap { x =>
            (for {
              desc  <- x.description.value.value
              units <- x.units.value.value
              qty   <- x.quantity.value.value
            } yield Details(desc, qty, units)).toList
          })

      own ++ fromHor

    def getSpaces(e: SurveyEntity): List[Details] =
      e.items.getOrElse(Nil)
        .filter(x => x.`type`.code == "HOR" && (x.`class`.code == "BUV" || x.`class`.code == "PUV"))
        .flatMap(_.data.uses.flatMap(u =>
          for {
            description <- u.description.value.value
            units       <- u.units.value.value
            area        <- u.quantity.value.value
          } yield Details(description, area, units)
        ))

    def recurse(e: SurveyEntity): ReviewDetails = {
      val childDetails: Seq[ReviewDetails] = e.items.getOrElse(Nil).map(recurse)
      val isFloor                          = e.`type`.code == "VER" && e.`class`.code == "BLV"
      val isParking                        = e.`type`.code == "VER" && e.`class`.code == "PLV"

      val facilities  = getFacilities(e)
      val spaces      = getSpaces(e)
      val currentArea = spaces.map(_.quantity).sum

      val floors  = (if (isFloor) List(LevelSummary(e.label, facilities, spaces, currentArea)) else Nil) ++ childDetails.flatMap(_.floorsInfo)
      val parking = (if (isParking) List(LevelSummary(e.label, facilities, spaces, currentArea)) else Nil) ++ childDetails.flatMap(_.parkingInfo)
      val total   = currentArea + childDetails.flatMap(_.floorsInfo).map(_.totalArea).sum

      ReviewDetails(floors, parking, total)
    }

    recurse(entity)
  }
}
