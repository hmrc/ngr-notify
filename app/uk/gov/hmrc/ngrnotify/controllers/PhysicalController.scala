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
import uk.gov.hmrc.ngrnotify.connectors.bridge.BridgeConnector
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{AssessmentId, PropertyChangesRequest}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PhysicalController @Inject() (
  bridgeConnector: BridgeConnector,
  cc: ControllerComponents,
  identifierAction: IdentifierAction
)(implicit ec: ExecutionContext
) extends BackendController(cc)
  with JsonSupport
  with Logging {

  def updatePropertyChanges(assessmentId: AssessmentId): Action[JsValue] = identifierAction.async(parse.json) { implicit request =>
    request.body.validate[PropertyChangesRequest] match {
      case JsSuccess(propertyChanges, _) =>
        bridgeConnector.submitPhysicalPropertyChanges(request.providerId, assessmentId, propertyChanges).toHttpResult()
      case jsError: JsError              => Future.successful(buildValidationErrorsResponse(jsError))
    }
  }
}
