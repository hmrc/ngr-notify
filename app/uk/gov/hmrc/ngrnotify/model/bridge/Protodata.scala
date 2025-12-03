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

// #/$defs/COMMON/PROTODATA/ITEMS
case class Protodata(
  // TODO Review the definition of the following fields
  node: Option[String],
  `mime_type`: String, // TODO Change to ProtoDataMimeType once the upstream systems support it
  label: String,
  is_pointer: Option[Boolean], // TODO as per schema it is not optional
  pointer: String,
  data: String
)

object Protodata:
  import play.api.libs.json.{Format, Json}
  given Format[Protodata] = Json.format
