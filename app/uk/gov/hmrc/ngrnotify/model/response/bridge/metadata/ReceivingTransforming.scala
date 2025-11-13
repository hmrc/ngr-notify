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

package uk.gov.hmrc.ngrnotify.model.response.bridge.metadata

import play.api.libs.json.{Json, OFormat}

case class ReceivingTransforming(
  recontextualising: Recontextualising,
  dropping: Dropping,
  restoring: Restoring
)

object ReceivingTransforming {
  implicit val format: OFormat[ReceivingTransforming] = Json.format[ReceivingTransforming]
}

case class Dropping()

object Dropping {
  implicit val format: OFormat[Dropping] = Json.format[Dropping]
}

case class Restoring()

object Restoring {
  implicit val format: OFormat[Restoring] = Json.format[Restoring]
}
