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

import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

// #/$defs/ENTITIES/PERSONS/PERSON/ENTITY
case class PersonEntity(
  id: NullableValue[Id],
  idx: String,
  name: NullableValue[String],
  label: String,
  description: NullableValue[String],
  origination: NullableValue[String],
  termination: NullableValue[String],
  // #/$defs/TAXONOMY/CAT_LTX-DOM-PSN
  category: CodeMeaning,
  `type`: CodeMeaning,
  `class`: CodeMeaning,
  // #/$defs/ENTITIES/PERSONS/PERSONS/DATA
  data: PersonData,
  protodata: List[Protodata],
  metadata: Metadata,
  compartments: Compartments,
  items: List[PersonEntity]
) extends Entity[PersonData, Compartments, List[PersonEntity]]
  with StandardProperties

object PersonEntity:
  import play.api.libs.json.*

  given Format[PersonEntity] = Json.format
