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

package uk.gov.hmrc.ngrnotify.controllers

import play.api.Logging
import play.api.libs.json.*
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.Government_Gateway
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RegisterRatepayerRequest, RegisterRatepayerResponse, RegistrationStatus}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
@Singleton
class RatepayerController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
  with JsonSupport
  with Logging:

  def registerRatepayer: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val result = request.body.validate[RegisterRatepayerRequest] match {
      case JsSuccess(registerRatepayer, _) =>
        logger.info(s"Request:\n$registerRatepayer")

        val bridgeRequest = toBridgeRequest(registerRatepayer)
        logger.info(s"BridgeRequest:\n$bridgeRequest")

        Accepted(Json.toJsObject(RegisterRatepayerResponse(RegistrationStatus.OK)))
      case jsError: JsError                => buildValidationErrorsResponse(jsError)
    }

    Future.successful(result)
  }

  private def toBridgeRequest(ratepayer: RegisterRatepayerRequest): BridgeRequest =
    BridgeRequest(
      Job(
        id = None,
        idx = "1",
        name = "Register Ratepayer",
        compartments = Compartments(
          products = List(
            Person(
              id = None,
              idx = "1.4.1",
              name = "Government Gateway User",
              data = PersonData(
                foreignIds = List(
                  ForeignId(
                    system = Some(Government_Gateway),
                    value = Some(ratepayer.ratepayerCredId)
                  ),
                  ForeignId(
                    location = Some("NINO"),
                    value = ratepayer.nino
                  ),
                  ForeignId(
                    location = Some("secondary_telephone_number"),
                    value = ratepayer.secondaryNumber
                  )
                ),
                foreignLabels = List(
                  ForeignId(
                    location = Some("RatepayerType"),
                    value = ratepayer.userType.map(_.toString)
                  ),
                  ForeignId(
                    location = Some("AgentStatus"),
                    value = ratepayer.agentStatus.map(_.toString)
                  )
                ),
                names = extractNames(ratepayer),
                communications = extractCommunications(ratepayer)
              )
            )
          )
        )
      )
    )

  private def extractForenamesAndSurname(fullName: String): (Option[String], Option[String]) =
    val trimmedFullName      = fullName.trim
    val index                = trimmedFullName.lastIndexOf(" ")
    val (forenames, surname) =
      if index == -1 then ("", trimmedFullName) else trimmedFullName.splitAt(index)
    (Option.when(forenames.trim.nonEmpty)(forenames.trim), Some(surname.trim))

  private def extractNames(ratepayer: RegisterRatepayerRequest): Names =
    val (forenamesOpt, surnameOpt) = extractForenamesAndSurname(ratepayer.name)

    Names(
      forenames = forenamesOpt,
      surname = surnameOpt,
      corporateName = ratepayer.tradingName
    )

  private def extractCommunications(ratepayer: RegisterRatepayerRequest): Communications =
    Communications(
      postalAddress = Some(ratepayer.address.singleLine),
      telephoneNumber = Some(ratepayer.contactNumber),
      email = Some(ratepayer.email)
    )
