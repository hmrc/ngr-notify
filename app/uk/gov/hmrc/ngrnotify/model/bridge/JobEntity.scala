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

import Bridge.{EmptyItems, Id}

// #/$defs/ENTITIES/JOBS/JOB/ENTITY
case class JobEntity(
  id: Option[Id],
  idx: String,
  name: Option[String],
  label: String,
  description: Option[String],
  origination: Option[String],
  termination: Option[String],
  protodata: List[Protodata],
  metadata: Metadata,

  // #/$defs/TAXONOMY/CAT_LTX-DOM-JOB_LOG
  category: CodeMeaning,
  `type`: CodeMeaning,
  `class`: CodeMeaning,

  // #/$defs/ENTITIES/JOBS/JOB/DATA
  data: JobData,
  compartments: Compartments,
  items: EmptyItems
) extends Entity[JobData, Compartments, EmptyItems]
  with StandardProperties

object JobEntity:
  import play.api.libs.json.*
  import Bridge.given
  given Format[JobEntity] = Json.format
  // See https://docs.scala-lang.org/scala3/book/ca-context-parameters.html#given-instances-implicit-definitions-in-scala-2
