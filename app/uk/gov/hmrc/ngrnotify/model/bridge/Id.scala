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

import play.api.libs.json.*

sealed trait Id
case object NullId extends Id
case class IntId(value: Int) extends Id
case class StringId(value: String) extends Id

implicit val idReads: Reads[Id] = Reads[Id] {
  case JsNull                                                             => JsSuccess(NullId)
  case JsNumber(n) if n.isValidInt && n.toInt >= 1 && n.toInt <= 99999999 =>
    JsSuccess(IntId(n.toInt))
  case JsString(s) if s.length == 36                                      => JsSuccess(StringId(s))
  case _                                                                  => JsError("Invalid id")
}

implicit val idWrites: Writes[Id] = Writes[Id] {
  case NullId          => JsNull
  case IntId(value)    => JsNumber(value)
  case StringId(value) => JsString(value)
}
