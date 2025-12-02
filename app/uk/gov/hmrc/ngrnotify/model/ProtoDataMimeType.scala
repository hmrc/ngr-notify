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

package uk.gov.hmrc.ngrnotify.model

import play.api.libs.json.Format
import uk.gov.hmrc.ngrnotify.model.Scala3EnumJsonFormat

enum ProtoDataMimeType:
  case Jpeg, Png, Pdf

  override def toString: String = this match
    case Jpeg => "image/jpeg"
    case Png  => "image/png"
    case Pdf  => "image/pdf"

object ProtoDataMimeType:
  given Format[ProtoDataMimeType] = Scala3EnumJsonFormat.format
