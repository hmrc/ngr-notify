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

import play.api.libs.json.*

/**
  * @author Yuriy Tumakha
  */
sealed trait System

object System {
  case object GovernmentGateway extends System
  case object OneLogin extends System
  case object BillingAuthority extends System
  case object CompaniesHouse extends System
  case object HmrcVoaCdb extends System
  case object NationalAddressGazetteer extends System

  val values: Set[System] = Set(GovernmentGateway, BillingAuthority, CompaniesHouse, HmrcVoaCdb, NationalAddressGazetteer)

  implicit val formatForeignIdSystem: Format[System] = Format(
    Reads {
      case JsString("Government_Gateway")         => JsSuccess(GovernmentGateway)
      case JsString("One_Login")                  => JsSuccess(OneLogin)
      case JsString("Billing_Authority")          => JsSuccess(BillingAuthority)
      case JsString("Companies_House")            => JsSuccess(CompaniesHouse)
      case JsString("HMRC-VOA_CDB")               => JsSuccess(HmrcVoaCdb)
      case JsString("National_Address_Gazetteer") => JsSuccess(NationalAddressGazetteer)
      case _                                      => JsError("Unknown ForeignIdSystem")
    },
    Writes {
      case GovernmentGateway        => JsString("Government_Gateway")
      case OneLogin                 => JsString("One_Login")
      case BillingAuthority         => JsString("Billing_Authority")
      case CompaniesHouse           => JsString("Companies_House")
      case HmrcVoaCdb               => JsString("HMRC-VOA_CDB")
      case NationalAddressGazetteer => JsString("National_Address_Gazetteer")
    }
  )
}
