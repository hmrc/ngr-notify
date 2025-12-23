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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

case class ED(
  activity: EDProperty[String],
  code: EDProperty[String],
  description: EDProperty[String],
  quantity: EDProperty[BigInt],
  units: EDProperty[String]
)

object ED:
  given Format[ED] = Json.format

case class EDProperty[T](
  source: NullableValue[String], // null or string (1-255 chars)
  value: NullableValue[T] // null or value
)

object EDProperty:
  given [T: Format]: Format[EDProperty[T]] = Json.format

case class ValuationSurveysData(
  foreign_ids: List[ForeignDatum] = List.empty,
  foreign_names: List[ForeignDatum] = List.empty,
  foreign_labels: List[ForeignDatum] = List.empty,
  survey: SurveyEntity
)

object ValuationSurveysData:
  given Format[ValuationSurveysData] = Json.format

case class ContainerData(
  foreign_ids: List[ForeignDatum] = List.empty,
  foreign_names: List[ForeignDatum] = List.empty,
  foreign_labels: List[ForeignDatum] = List.empty,
  uses: List[ED] = List.empty,
  constructions: List[ED] = List.empty,
  facilities: List[ED] = List.empty,
  artifacts: List[ED] = List.empty,
  uninheritances: List[ED] = List.empty,
  attributions: List[ED] = List.empty
)

object ContainerData:
  given Format[ContainerData] = Json.format

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

object SurveyEntity:
    given Format[SurveyEntity] = Json.format


