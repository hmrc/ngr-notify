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
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*
import uk.gov.hmrc.ngrnotify.model.bridge.{Bridge, BridgeRequest, Compartments}
import uk.gov.hmrc.ngrnotify.model.propertyDetails.PropertyLinkingRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertyController @Inject() (
  @deprecated hipConnector: HipConnector,
  cc: ControllerComponents
)(implicit ec: ExecutionContext
) extends BackendController(cc)
  with JsonSupport
  with Logging {

  @deprecated(
    message = "This needs to be re-implemented using the new BridgeConnector",
    since = "2025-11-25"
  )
  def submit(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[PropertyLinkingRequest] match {
      case JsSuccess(propertyRequest, _) =>
        val bridgeRequest = toBridgeRequest(propertyRequest)
        hipConnector.submitPropertyLinkingChanges(bridgeRequest).map { response =>
          response.status match {
            case status if is2xx(status) => Accepted
            case 400                     => BadRequest
            case status                  => InternalServerError(buildFailureResponse(WRONG_RESPONSE_STATUS, s"$status ${response.body}"))
          }
        }
          .recover(e =>
            InternalServerError(buildFailureResponse(ACTION_FAILED, e.getMessage))
          )

      case jsError: JsError => Future.successful(buildValidationErrorsResponse(jsError))
    }
  }

  private def toBridgeRequest(propertyRequest: PropertyLinkingRequest): BridgeRequest =
    // Conversion of messages is going to be moved to the new BridgeConnector
    ???

}
