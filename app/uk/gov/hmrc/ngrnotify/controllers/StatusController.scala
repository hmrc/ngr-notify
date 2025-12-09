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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.model.ErrorCode
import uk.gov.hmrc.ngrnotify.model.response.{ApiFailure, RatepayerStatusResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class StatusController @Inject() (
  cc: ControllerComponents,
  identifierAction: IdentifierAction,
  @deprecated hipConnector: HipConnector
)(implicit ec: ExecutionContext
) extends BackendController(cc) {

  def buildFailureResponse(code: ErrorCode, reason: String): JsValue =
    Json.toJson(Seq(ApiFailure(code, reason)))

  @deprecated(
    message = "This needs to be re-implemented using the new BridgeConnector",
    since = "2025-11-25"
  )
  def getRatepayerStatus: Action[AnyContent] = identifierAction.async { implicit request =>

    Future.successful(Ok(
      Json.toJsObject(
        RatepayerStatusResponse(
          false,
          false,
          0
        )
      )
    ))

//    hipConnector.getRatepayerStatus(id)
//      .map {
//        response =>
//          response.status match {
//            case 200    => response.json.validate[RatepayerStatusResponse] match {
//                case JsSuccess(value, path) => Ok(
//                    Json.toJsObject(
//                      RatepayerStatusResponse(
//                        value.activeRatepayerPersonaExists,
//                        value.activeRatepayerPersonaExists,
//                        value.activePropertyLinkCount
//                      )
//                    )
//                  )
//                case JsError(errors)        => BadRequest
//              }
//            case status => InternalServerError(buildFailureResponse(WRONG_RESPONSE_STATUS, s"$status ${response.body}"))
//          }
//      }
//      .recover(e => InternalServerError(buildFailureResponse(ACTION_FAILED, e.getMessage)))
  }
}
