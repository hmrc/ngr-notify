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

package uk.gov.hmrc.ngrnotify.model.ratepayer

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrnotify.model.Address
import uk.gov.hmrc.ngrnotify.model.email.Email

/**
  * @author Yuriy Tumakha
  */
final case class RegisterRatepayerRequest(
  ratepayerCredId: String,
  userType: Option[RatepayerType] = None,
  agentStatus: Option[AgentStatus] = None,
  name: Option[Name],
  tradingName: Option[TradingName] = None,
  email: Option[Email] = None,
  nino: Option[Nino] = None,
  contactNumber: Option[PhoneNumber] = None,
  secondaryNumber: Option[PhoneNumber] = None,
  address: Option[Address] = None,
  trnReferenceNumber: Option[TRNReferenceNumber] = None,
  isRegistered: Option[Boolean] = Some(false)
)

object RegisterRatepayerRequest:
  implicit val format: OFormat[RegisterRatepayerRequest] = Json.format
