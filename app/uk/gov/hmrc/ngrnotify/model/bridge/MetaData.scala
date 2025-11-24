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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.{Receiving, Sending}

case class Metadata(
  sending: Sending,
  receiving: Receiving
)

object Metadata {
  implicit val metadataFormat: OFormat[Metadata] = Json.format[Metadata]
}

case class Signing(
  inputs: Option[SigningInputs] = None
)

object Signing {
  implicit val signingFormat: OFormat[Signing] = Json.format[Signing]
}

case class SigningInputs(
  hash: String,
  signature: Option[String]
)

object SigningInputs {
  implicit val signingInputsFormat: OFormat[SigningInputs] = Json.format[SigningInputs]
}
