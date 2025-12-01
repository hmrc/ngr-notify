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
import uk.gov.hmrc.ngrnotify.model.given

case class AssessmentEntity(
                             id: Option[Id],
                             idx: String,
                             name: Option[String],
                             label: String,
                             description: Option[String],
                             origination: Option[String],
                             termination: Option[String],
                             category: CodeMeaning,
                             `type`: CodeMeaning,
                             `class`: CodeMeaning,
                             data: AssessmentData,
                             protodata: List[Protodata],
                             metadata: Metadata,
                             compartments: Option[Map[String, String]] = None,
                             items: Option[List[String]] = None
                           ) extends Entity[AssessmentData, Option[Map[String, String]], Option[List[String]]]
  with StandardProperties

object AssessmentEntity:
  given Format[AssessmentEntity] = Json.format

case class AssessmentData(
                           foreignIds: List[ForeignDatum] = List.empty,
                           foreignNames: List[ForeignDatum] = List.empty,
                           foreignLabels: List[ForeignDatum] = List.empty,
                           property: AssessmentProperty,
                           use: AssessmentUse,
                           valuation_surveys: Seq[AssessmentContainerEntity],
                           valuations: List[String] = List.empty,
                           valuation: AssessmentValuation,
                           list: AssessmentList,
                           workflow: AssessmentWorkflow
                         )

object AssessmentData:
  given Format[AssessmentData] = Json.format

case class AssessmentProperty(
                               id: Int,
                               cdb_id: String
                             )

object AssessmentProperty:
  given Format[AssessmentProperty] = Json.format

case class AssessmentUse(
                          is_composite: String, // "Y" or "N"
                          is_part_exempt: String,
                          description: String
                        )

object AssessmentUse:
  given Format[AssessmentUse] = Json.format

/*
case class AssessmentIsPartExempt(
                                   is_composite: String // "Y" or "N"
                                 )

object AssessmentIsPartExempt:
  given Format[AssessmentIsPartExempt] = Json.format
*/


case class AssessmentValuation(
                                method_code: String,
                                rateable_value: Long,
                                effective_date: String
                              )

object AssessmentValuation:
  given Format[AssessmentValuation] = Json.format

case class AssessmentList(
                           category: String = "LTX-DOM-LST",
                           function: String,
                           year: String,
                           authority_code: String
                         )

object AssessmentList:
  given Format[AssessmentList] = Json.format


case class AssessmentWorkflow(
                               cdb_job_id: Option[String] = None,
                             )

object AssessmentWorkflow:
  given Format[AssessmentWorkflow] = Json.format

case class AssessmentContainerEntity(
                                      id: Option[Id],
                                      idx: String,
                                      name: Option[String],
                                      label: String,
                                      description: Option[String],
                                      origination: Option[String],
                                      termination: Option[String],
                                      category: CodeMeaning,
                                      `type`: CodeMeaning,
                                      `class`: CodeMeaning,
                                      data: ContainerData,
                                      protodata: List[Protodata],
                                      metadata: Metadata,
                                      compartments: Option[Map[String, String]] = None,
                                      items: Option[List[AssessmentContainerEntity]] = None
                                    ) extends Entity[ContainerData, Option[Map[String, String]], Option[List[AssessmentContainerEntity]]]
  with StandardProperties


object AssessmentContainerEntity:
  given Format[AssessmentContainerEntity] = Json.format