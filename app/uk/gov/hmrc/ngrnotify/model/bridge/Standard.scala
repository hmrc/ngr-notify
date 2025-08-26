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

// #/$defs/ENTITIES/STANDARD
trait Standard:
  val id: Option[Id]
  val idx: String
  val name: String
  // val label: String
  // val description: String
  // val origination: Option[#/$defs/COMMON/INSTANT]
  // val termination: Option[#/$defs/COMMON/INSTANT]
  // val protodata: List[#/$defs/COMMON/PROTODATA/ITEMS]
  // val metadata: Metadata
