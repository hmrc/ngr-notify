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

case class Loading(assuring: Assuring,
                   readying: Readying,
                   signing: Signing,
                   encrypting: Encrypting,
                   sending: LoadingSending)

object Loading {
  implicit val format: OFormat[Loading] = Json.format[Loading]
}

case class Signing()

object Signing {
  implicit val format: OFormat[Signing] = Json.format[Signing]
}

case class Encrypting()

object Encrypting {
  implicit val format: OFormat[Encrypting] = Json.format[Encrypting]
}

case class LoadingSending()

object LoadingSending {
  implicit val format: OFormat[LoadingSending] = Json.format[LoadingSending]
}
