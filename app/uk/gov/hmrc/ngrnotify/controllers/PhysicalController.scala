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
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import play.api.mvc.{Action, ControllerComponents, Request}
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.model.bridge.{BridgeJobModel, BridgeRequest, Compartments, Job}
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{CredId, PropertyChangesRequest, PropertyChangesResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.{JobItem, MetadataAction, MetadataStage, MetadataTransform}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PhysicalController @Inject() (
  hipConnector: HipConnector,
  cc: ControllerComponents
)(implicit ec: ExecutionContext
) extends BackendController(cc)
  with JsonSupport
  with Logging {

  private def getRatepayer(credId: CredId)(implicit request: Request[JsValue]): Future[Option[BridgeJobModel]] =
    hipConnector.getRatepayer(credId)
      .map { response =>
        response.status match {
          case OK =>
            response.json.validate[BridgeJobModel].asOpt
          case _ =>
            None
        }
      }

  def updatePropertyChanges(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[PropertyChangesRequest] match {
      case JsSuccess(propertyChanges, _) =>

        getRatepayer(propertyChanges.credId) flatMap {
          case Some(ratePayerJob) =>
            val bridgeRequest = toBridgeRequest(ratePayerJob, propertyChanges)
            hipConnector.updatePropertyChanges(bridgeRequest).map { response =>
                response.status match {
                  case status if is2xx(status) => Accepted(Json.toJsObject(PropertyChangesResponse()))
                  case BAD_REQUEST => BadRequest(Json.toJsObject(PropertyChangesResponse(Some(response.body))))
                  case status => InternalServerError(buildFailureResponse(WRONG_RESPONSE_STATUS, s"$status ${response.body}"))
                }
              }
              .recover(e =>
                InternalServerError(buildFailureResponse(ACTION_FAILED, e.getMessage))
              )

          case _ =>
            logger.warn(s"No ratepayer data found from HIP for CredId: ${propertyChanges.credId}")
            Future.successful(NotFound)
        }

      case jsError: JsError => Future.successful(buildValidationErrorsResponse(jsError))
    }
  }

  private def toBridgeRequest(ratePayerJobModel: BridgeJobModel, propertyChanges: PropertyChangesRequest): BridgeJobModel = {
    val toBridgeJob = JobItem(
      id = None,
      idx = Some("?"),
      name = Some("physical"),
      label = Some("Physical Job"),
      description = Some("Default physical job item"),
      origination = None,
      termination = None,
      category = BridgeJobModel.CodeMeaning(None, None),
      `type` = BridgeJobModel.CodeMeaning(None, None),
      `class` = BridgeJobModel.CodeMeaning(None, None),
      data = BridgeJobModel.Data(Nil, Nil, Nil),
      protodata = Seq.empty,
      metadata = BridgeJobModel.Metadata(MetadataStage(), MetadataStage()),
      compartments = BridgeJobModel.Compartments(),
      items = None
    )

    val updatedProperties = ratePayerJobModel.job.compartments.properties :+ toBridgeJob
    val updatedCompartments = ratePayerJobModel.job.compartments.copy(properties = updatedProperties)
    val updatedJob = ratePayerJobModel.job.copy(compartments = updatedCompartments)
    ratePayerJobModel.copy(job = updatedJob)
  }
}