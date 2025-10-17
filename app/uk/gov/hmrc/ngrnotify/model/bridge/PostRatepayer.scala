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

// ---------- Case Classes ----------

case class PostRatepayer(
                    $schema: String,
                    job: Job
                  )

object PostRatepayer {
  case class Job(
                  id: Option[String],
                  idx: String,
                  name: String,
                  label: String,
                  description: String,
                  origination: Option[String],
                  termination: Option[String],
                  category: CodeMeaning,
                  `type`: CodeMeaning,
                  `class`: CodeMeaning,
                  data: Data,
                  protodata: Seq[Protodata],
                  metadata: Metadata,
                  compartments: Compartments,
                  items: Seq[JobItem]
                )

  case class CodeMeaning(code: String, meaning: String)

  case class Data(
                   foreign_ids: Seq[String],
                   foreign_names: Seq[String],
                   foreign_labels: Seq[String],
                   names: Option[Names] = None,
                   communications: Option[Communications] = None
                 )

  case class Names(
                    title_common: Option[String],
                    title_uncommon: Option[String],
                    forenames: Option[String],
                    surname: Option[String],
                    post_nominals: Option[String],
                    corporate_name: Option[String],
                    crown_name: Option[String],
                    known_as: Option[String]
                  )

  case class Communications(
                             postal_address: Option[String],
                             telephone_number: Option[String],
                             email: Option[String]
                           )

  case class Protodata() // empty placeholder

  case class Metadata(
                       sending: MetadataStage,
                       receiving: MetadataStage
                     )

  case class MetadataStage(
                            extracting: MetadataAction = MetadataAction(),
                            transforming: MetadataTransform = MetadataTransform(),
                            loading: Option[MetadataLoading] = None,
                            unloading: Option[MetadataUnloading] = None,
                            storing: Option[MetadataStoring] = None
                          )

  case class MetadataAction(selecting: Map[String, String] = Map.empty)

  case class MetadataTransform(
                                filtering: Map[String, String] = Map.empty,
                                supplementing: Map[String, String] = Map.empty,
                                recontextualising: Map[String, String] = Map.empty,
                                dropping: Map[String, String] = Map.empty,
                                restoring: Map[String, String] = Map.empty
                              )

  case class MetadataLoading(
                              readying: Map[String, String] = Map.empty,
                              assuring: Map[String, String] = Map.empty,
                              signing: Map[String, String] = Map.empty,
                              encrypting: Map[String, String] = Map.empty,
                              sending: Map[String, String] = Map.empty
                            )

  case class MetadataUnloading(
                                receiving: Map[String, String] = Map.empty,
                                decrypting: Map[String, String] = Map.empty,
                                verifying: Map[String, String] = Map.empty,
                                assuring: Map[String, String] = Map.empty,
                                readying: Map[String, String] = Map.empty
                              )

  case class MetadataStoring(inserting: Map[String, String] = Map.empty)

  case class Compartments(
                           properties: Seq[JobItem] = Seq.empty,
                           persons: Seq[JobItem] = Seq.empty,
                           processes: Seq[JobItem] = Seq.empty,
                           products: Seq[JobItem] = Seq.empty,
                           relationships: Seq[JobItem] = Seq.empty
                         )

  case class JobItem(
                      id: Option[String],
                      idx: String,
                      name: String,
                      label: String,
                      description: String,
                      origination: Option[String],
                      termination: Option[String],
                      category: CodeMeaning,
                      `type`: CodeMeaning,
                      `class`: CodeMeaning,
                      data: Data,
                      protodata: Seq[Protodata],
                      metadata: Metadata,
                      compartments: Compartments,
                      items: Seq[JobItem]
                    )

  // ---------- Play JSON Implicits ----------

  import play.api.libs.json._

  implicit val protodataFormat: OFormat[Protodata] = Json.format[Protodata]
  implicit val codeMeaningFormat: OFormat[CodeMeaning] = Json.format[CodeMeaning]
  implicit val namesFormat: OFormat[Names] = Json.format[Names]
  implicit val communicationsFormat: OFormat[Communications] = Json.format[Communications]
  implicit val dataFormat: OFormat[Data] = Json.format[Data]
  implicit val metadataActionFormat: OFormat[MetadataAction] = Json.format[MetadataAction]
  implicit val metadataTransformFormat: OFormat[MetadataTransform] = Json.format[MetadataTransform]
  implicit val metadataLoadingFormat: OFormat[MetadataLoading] = Json.format[MetadataLoading]
  implicit val metadataUnloadingFormat: OFormat[MetadataUnloading] = Json.format[MetadataUnloading]
  implicit val metadataStoringFormat: OFormat[MetadataStoring] = Json.format[MetadataStoring]

  implicit lazy val metadataStageFormat: OFormat[MetadataStage] = Json.format[MetadataStage]
  implicit lazy val metadataFormat: OFormat[Metadata] = Json.format[Metadata]
  implicit lazy val compartmentsFormat: OFormat[Compartments] = Json.format[Compartments]

  implicit lazy val jobItemFormat: OFormat[JobItem] = Json.format[JobItem]
  implicit lazy val jobFormat: OFormat[Job] = Json.format[Job]
  implicit lazy val jobRootFormat: OFormat[PostRatepayer] = Json.format[PostRatepayer]

}



