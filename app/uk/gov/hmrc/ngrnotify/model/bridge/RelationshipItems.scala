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

case class PointerTransportation(
  path: Option[String] // null or string (minLength: 1, maxLength: 255)
)

object PointerTransportation:
  import play.api.libs.json.{Json, OFormat}
  implicit val pointerTransportationFormat: OFormat[PointerTransportation] = Json.format[PointerTransportation]

case class PointerPersistence(
  place: String, // reference to CAT_LTX-DOM taxonomy
  identifier: Int // integer (min: 1, max: 99999999)
)

object PointerPersistence:
  import play.api.libs.json.{Json, OFormat}
  implicit val pointerPersistenceFormat: OFormat[PointerPersistence] = Json.format[PointerPersistence]

case class Pointer(
  transportation: PointerTransportation,
  persistence: PointerPersistence
)

object Pointer:
  import play.api.libs.json.{Json, OFormat}
  implicit val pointerFormat: OFormat[Pointer] = Json.format[Pointer]
