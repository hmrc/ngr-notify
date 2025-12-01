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

import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.EmptyItems

// #/$defs/ENTITIES/PROCESSES/PROCESS
case class ProcessEntity(
  id: Option[Id],
  idx: String,
  name: Option[String],
  label: String,
  description: Option[String],
  origination: Option[String],
  termination: Option[String],
  protodata: List[Protodata],
  metadata: Metadata,
  category: CodeMeaning,
  `type`: CodeMeaning,
  `class`: CodeMeaning,
  data: RelationshipData,
  compartments: EmptyCompartments,
  // TODO Reconsider these items as the Json Schema defines them of type #/$defs/ENTITIES/RELATIONSHIPS/ASSOCIATES/ENTITY instead
  items: EmptyItems
) extends Entity[RelationshipData, EmptyCompartments, EmptyItems]
  with StandardProperties

object ProcessEntity:
  import play.api.libs.json.{Format, Json}
  given Format[ProcessEntity] = Json.format
