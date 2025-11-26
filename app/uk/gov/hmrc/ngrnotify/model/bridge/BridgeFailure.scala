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

/**
  * Represent a response from the Bridge API service which indicates a failure caused by a known reason.
  */
case class BridgeFailure(
  `type`: String,
  reason: String
)

object BridgeFailure:
  given Format[BridgeFailure] = Json.format
  def unknown()               = BridgeFailure("", "Unknown reason")
