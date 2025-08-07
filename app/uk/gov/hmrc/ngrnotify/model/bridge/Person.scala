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

import Bridge.Id

// #/$defs/ENTITIES/PERSONS/PERSON
case class Person (
  // #/$defs/ENTITIES/STANDARD
  id: Option[Id],
  idx: String,
  name: String,
  // label: String
  // description: String
  // origination: Option[#/$defs/COMMON/INSTANT]
  // termination: Option[#/$defs/COMMON/INSTANT]
  // protodata: List[#/$defs/COMMON/PROTODATA/ITEMS]
  // metadata: Metadata

  // #/$defs/TAXONOMY/CAT_LTX-DOM-PSN_LOG

  // category: #/$defs/COMMON/ENUMERATION[CAT_LTX-DOM]
  // type: #/$defs/COMMON/ENUMERATION[EMPTY]
  // class: #/$defs/COMMON/ENUMERATION[EMPTY]
  data: PersonData
  // compartments: Empty
  // items: List[#/$defs/ENTITIES/PERSONS/PERSON]

) extends Standard


object Person:
    import play.api.libs.json.*
    import Bridge.given

    given Format[Person] = Json.format