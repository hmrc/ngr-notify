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
import uk.gov.hmrc.ngrnotify.connectors.bridge.BridgeConnector
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RatepayerPropertyLinksResponse, RegisterRatepayerRequest, RegisterRatepayerResponse, RegistrationStatus}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class RatepayerController @Inject() (
  @deprecated hipConnector: HipConnector,
  bridgeConnector: BridgeConnector,
  identifierAction: IdentifierAction,
  cc: ControllerComponents
)(implicit ec: ExecutionContext
) extends BackendController(cc)
  with JsonSupport
  with Logging:

  /*
   *  DESIGN NOTES
   *  ------------
   *
   *    1. This controller leverages the JsonSupport trait as that provides utilities for
   *       handling Bridge failure messages and converting them to propert HTTP responses.
   *
   *    2. This controller's actions (such as the registerPayer() action) attempt to JSON validate
   *       the incoming request body against one of our NgrMessage models (such as the RegisterRatepayerRequest)
   *
   *       2.1 If the request body is valid, the action delegates to the new BridgeConnector
   *            (for example, invoking the registerRatepayer() method) and the
   *            converts the BridgeResult (either a successful result or a failure result)
   *            into a propert HTTP response.
   *
   *       2.2 If the request body is invalid, the action builds and returns a 400 Bad Request response
   *           with a validation error message
   */
  def registerRatepayer: Action[JsValue] = identifierAction.async(parse.json) { implicit request =>
    request.body.validate[RegisterRatepayerRequest] match {
      case JsSuccess(ngrRequest, _) => bridgeConnector.registerRatepayer(ngrRequest).toHttpResult()
      case jsError: JsError         => Future.successful(buildValidationErrorsResponse(jsError))
    }
  }

  @deprecated(
    message = "This has to be re-implemented using the new BridgeConnector",
    since = "2025-11-25"
  )
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

  @deprecated(
    message = "This may No longer needed as the  BridgeConnector si taking over the HipConnector",
    since = "2025-11-25"
  )
  private def parsePropertyLinks(response: String): Result =
    Try(Json.parse(response)).map {
      _.validate[BridgeResponse] match {
        case JsSuccess(bridgeResponse, _) =>
          logger.info(s"Bridge Response:\n$bridgeResponse")

          val addresses: Seq[String] = bridgeResponse.job.compartments.properties
            .map(_.data.addresses.propertyFullAddress.getOrElse(""))
          Ok(Json.toJsObject(RatepayerPropertyLinksResponse(addresses.nonEmpty, addresses)))
        case jsError: JsError             => buildValidationErrorsResponse(jsError).copy(header = ResponseHeader(INTERNAL_SERVER_ERROR))
      }
    }.getOrElse {
      logger.warn(s"Bridge Response:\n$response")
      InternalServerError(buildFailureResponse(WRONG_RESPONSE_BODY, "HIP response could not be parsed into JSON format."))
    }
