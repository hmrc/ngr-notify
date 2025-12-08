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
import uk.gov.hmrc.ngrnotify.model.{Address, NgrNotifyMessage}
import uk.gov.hmrc.ngrnotify.model.email.Email

/**
  * @author Yuriy Tumakha
  */
final case class RegisterRatepayerRequest(
  userType: Option[RatepayerType] = None,
  agentStatus: Option[AgentStatus] = None,
  name: Option[Name] = None,
  tradingName: Option[TradingName] = None,
  email: Option[Email] = None,
  nino: Option[Nino] = None,
  contactNumber: Option[PhoneNumber] = None,
  secondaryNumber: Option[PhoneNumber] = None,
  address: Option[Address] = None,
  trnReferenceNumber: Option[TRNReferenceNumber] = None,
  isRegistered: Option[Boolean] = Some(false),
  recoveryId: Option[String] = None
) extends NgrNotifyMessage {

  // TODO Review this method as it's not clear how it would work for the following values
  //      "Mr. Sadiq Khan",
  //      "Elizabeth Harbor Fajid"
  //      Extracting the first and last name from the name field seems brittle and prone to errors

  val forenameAndSurname: (Option[String], Option[String]) =
    name.map {
      _.value.trim.split(" ").toList match
        case forename :: surname :: _ => (Some(forename), Some(surname))
        case forename :: Nil          => (Some(forename), None)
        case Nil                      => (None, None)
    }
      .getOrElse((None, None))
}

object RegisterRatepayerRequest:
  implicit val format: OFormat[RegisterRatepayerRequest] = Json.format
