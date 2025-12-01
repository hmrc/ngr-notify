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

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.WildcardType

// TODO Review all the models' definitions in this Scala file as they've been written in a rush

case class Sending(
  extracting: Extracting,
  transforming: Transforming,
  loading: Loading
)

object Sending:
  given Format[Sending] = Json.format

// ------------------

case class Extracting(
  selecting: WildcardType
)

object Extracting:
  given Format[Extracting] = Json.format

// ------------------

case class Transforming(
  filtering: WildcardType,
  supplementing: WildcardType,
  recontextualising: WildcardType
)

object Transforming:
  given Format[Transforming] = Json.format

// ------------------

case class Loading(
  readying: WildcardType,
  assuring: WildcardType,
  signing: WildcardType,
  encrypting: WildcardType,
  sending: WildcardType
)

object Loading:
  given Format[Loading] = Json.format

case class Signing(
                    inputs: Option[SigningInputs] = None
                  )

object Signing:
  given OFormat[Signing] = Json.format


case class SigningInputs(
                          hash: String,
                          signature: Option[String] = None
                        )

object SigningInputs:
  given OFormat[SigningInputs] = Json.format


