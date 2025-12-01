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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.WildcardType

// TODO Review all the models' definitions in this Scala file as they've been written in a rush

case class Receiving(
  unloading: Unloading,
  transforming: TransformingReceiving,
  storing: Storing
)

object Receiving:
  given Format[Receiving] = Json.format

// ------------------

case class Unloading(
  receiving: WildcardType,
  decrypting: WildcardType,
  verifying: WildcardType,
  assuring: WildcardType,
  readying: WildcardType
)

object Unloading:
  given Format[Unloading] = Json.format

// ------------------

case class TransformingReceiving(
  recontextualising: WildcardType,
  dropping: WildcardType,
  restoring: WildcardType
)

object TransformingReceiving:
  given Format[TransformingReceiving] = Json.format

// ------------------

case class Storing(
  inserting: WildcardType
)

object Storing:
  given Format[Storing] = Json.format
