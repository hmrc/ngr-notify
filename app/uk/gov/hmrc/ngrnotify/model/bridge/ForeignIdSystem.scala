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

import play.api.libs.json.Format

sealed trait ForeignIdSystem

object ForeignIdSystem {
  case object Government_Gateway extends ForeignIdSystem
  case object Billing_Authority extends ForeignIdSystem
  case object Companies_House extends ForeignIdSystem
  case object National_Address_Gazetteer extends ForeignIdSystem
  case object NDRRPublicInterface extends ForeignIdSystem
  case object HMRC_VOA_CDB extends ForeignIdSystem // Scala identifiers can't have '-'
  case object SystemX extends ForeignIdSystem // Scala identifiers can't have '-'
  case object CDB_VSA_SURVEY extends ForeignIdSystem // Scala identifiers can't have '-'

  val values: Set[ForeignIdSystem] = Set(
    Government_Gateway,
    Billing_Authority,
    Companies_House,
    National_Address_Gazetteer,
    NDRRPublicInterface,
    HMRC_VOA_CDB,
    SystemX,
    CDB_VSA_SURVEY
  )

  given Format[ForeignIdSystem] = new Format[ForeignIdSystem] {
    import play.api.libs.json.*

    override def writes(o: ForeignIdSystem): JsValue = JsString(o match {
      case Government_Gateway         => "Government_Gateway"
      case Billing_Authority          => "Billing_Authority"
      case Companies_House            => "Companies_House"
      case National_Address_Gazetteer => "National_Address_Gazetteer"
      case NDRRPublicInterface        => "NDRRPublicInterface"
      case HMRC_VOA_CDB               => "HMRC-VOA_CDB"
      case SystemX                    => "SystemX"
      case CDB_VSA_SURVEY                    => "CDB_VSA_SURVEY"
    })

    override def reads(json: JsValue): JsResult[ForeignIdSystem] = json match {
      case JsString("Government_Gateway")         => JsSuccess(Government_Gateway)
      case JsString("Billing_Authority")          => JsSuccess(Billing_Authority)
      case JsString("Companies_House")            => JsSuccess(Companies_House)
      case JsString("National_Address_Gazetteer") => JsSuccess(National_Address_Gazetteer)
      case JsString("NDRRPublicInterface")        => JsSuccess(NDRRPublicInterface)
      case JsString("HMRC-VOA_CDB")               => JsSuccess(HMRC_VOA_CDB)
      case JsString("SystemX")                    => JsSuccess(SystemX)
      case JsString("CDB_VSA_SURVEY")                    => JsSuccess(CDB_VSA_SURVEY)
      case x                                      => JsError(s"$x Unknown ForeignIdSystem")
    }
  }
}
