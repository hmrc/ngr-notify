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

case class SendingTransforming(recontextualising: Recontextualising,
                               filtering: Filtering,
                               supplementing: Supplementing
                              )

object SendingTransforming {
  implicit val format: OFormat[SendingTransforming] = Json.format[SendingTransforming]
}

case class Filtering()

object Filtering {
  implicit val format: OFormat[Filtering] = Json.format[Filtering]
}

case class Supplementing()

object Supplementing {
  implicit val format: OFormat[Supplementing] = Json.format[Supplementing]
}
