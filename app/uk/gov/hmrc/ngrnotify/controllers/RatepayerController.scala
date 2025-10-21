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
import play.api.mvc.*
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.Government_Gateway
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RatepayerPropertyLinksResponse, RegisterRatepayerRequest, RegisterRatepayerResponse, RegistrationStatus}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * @author Yuriy Tumakha
  */
@Singleton
class RatepayerController @Inject() (
  hipConnector: HipConnector,
  cc: ControllerComponents
)(implicit ec: ExecutionContext
) extends BackendController(cc)
  with JsonSupport
  with Logging:

  def registerRatepayer: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RegisterRatepayerRequest] match {
      case JsSuccess(registerRatepayer, _) =>
        logger.info(s"Request:\n$registerRatepayer")

        val bridgeRequest = toBridgeRequest(registerRatepayer)
        logger.info(s"BridgeRequest:\n$bridgeRequest")

        hipConnector.registerRatepayer(bridgeRequest)
          .map { response =>
            response.status match {
              case 200 | 201 | 202 => Accepted(Json.toJsObject(RegisterRatepayerResponse(RegistrationStatus.OK)))
              case 400             => BadRequest(Json.toJsObject(RegisterRatepayerResponse(RegistrationStatus.INCOMPLETE, Some(response.body))))
              case status          => InternalServerError(buildFailureResponse(WRONG_RESPONSE_STATUS, s"$status ${response.body}"))
            }
          }
          .recover(e => InternalServerError(buildFailureResponse(ACTION_FAILED, e.getMessage)))
      case jsError: JsError                => Future.successful(buildValidationErrorsResponse(jsError))
    }
  }

  def getRatepayerPropertyLinks(ratepayerCredId: String): Action[AnyContent] = Action.async { implicit request =>
    hipConnector.getRatepayer(ratepayerCredId)
      .map { response =>
        response.status match {
          case 200    => parsePropertyLinks(response.body)
          case status => InternalServerError(buildFailureResponse(WRONG_RESPONSE_STATUS, s"$status ${response.body}"))
        }
      }
      .recover(e => InternalServerError(buildFailureResponse(ACTION_FAILED, e.getMessage)))
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
                    value = Some(ratepayer.nino.map(_.value).getOrElse(""))
                  ),
                  ForeignId(
                    location = Some("secondary_telephone_number"),
                    value = Some(ratepayer.secondaryNumber.map(_.value).getOrElse(""))
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

  private def extractNames(ratepayer: RegisterRatepayerRequest): Option[Names] =
    val (forenamesOpt, surnameOpt) = extractForenamesAndSurname(ratepayer.name.map(_.value).getOrElse(""))

    Some(
      Names(
        forenames = forenamesOpt,
        surname = surnameOpt,
        corporateName = Some(ratepayer.tradingName.map(_.value).getOrElse(""))
      )
    )

  private def extractForenamesAndSurname(fullName: String): (Option[String], Option[String]) =
    val trimmedFullName      = fullName.trim
    val index                = trimmedFullName.lastIndexOf(" ")
    val (forenames, surname) =
      if index == -1 then ("", trimmedFullName) else trimmedFullName.splitAt(index)
    (Option.when(forenames.trim.nonEmpty)(forenames.trim), Some(surname.trim))

  private def extractCommunications(ratepayer: RegisterRatepayerRequest): Option[Communications] =
    Some(
      Communications(
        postalAddress = Some(ratepayer.address.map(_.singleLine).getOrElse("")),
        telephoneNumber = Some(ratepayer.contactNumber.map(_.value).getOrElse("")),
        email = Some(ratepayer.email.map(_.value).getOrElse(""))
      )
    )

  private def parsePropertyLinks(response: String): Result =
    Try(Json.parse(response)).map {
      _.validate[BridgeResponse] match {
        case JsSuccess(bridgeResponse, _) =>
          logger.info(s"Bridge Response:\n$bridgeResponse")

          val addresses: Seq[String] = bridgeResponse.job.compartments.properties
            .map(_.data.address.propertyFullAddress.getOrElse(""))
          Ok(Json.toJsObject(RatepayerPropertyLinksResponse(addresses.nonEmpty, addresses)))
        case jsError: JsError             => buildValidationErrorsResponse(jsError).copy(header = ResponseHeader(INTERNAL_SERVER_ERROR))
      }
    }.getOrElse {
      logger.warn(s"Bridge Response:\n$response")
      InternalServerError(buildFailureResponse(WRONG_RESPONSE_BODY, "HIP response could not be parsed into JSON format."))
    }
