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
  receiving: Map[String, String] = Map.empty,
  decrypting: Map[String, String] = Map.empty,
  verifying: Map[String, String] = Map.empty,
  assuring: Map[String, String] = Map.empty,
  readying: Map[String, String] = Map.empty
)

object Unloading:
  given Format[Unloading] = Json.format

// ------------------

case class TransformingReceiving(
  recontextualising: Map[String, String] = Map.empty,
  dropping: Map[String, String] = Map.empty,
  restoring: Map[String, String] = Map.empty
)

object TransformingReceiving:
  given Format[TransformingReceiving] = Json.format

// ------------------

case class Storing(
  inserting: Map[String, String] = Map.empty
)

object Storing:
  given Format[Storing] = Json.format
