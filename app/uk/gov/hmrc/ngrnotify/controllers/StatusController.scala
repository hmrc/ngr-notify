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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus
import uk.gov.hmrc.ngrnotify.model.response.RatepayerStatusResponse
import uk.gov.hmrc.ngrnotify.services.StatusService

@Singleton()
class StatusController @Inject() (cc: ControllerComponents)() extends BackendController(cc) {
  def ratepayerStatus(id: String): Action[AnyContent] = Action.async { implicit request =>
    val ratepayerStatus: RatepayerStatus                 = StatusService.checkRatepayerStatus(id)
    val ratepayerStatusResponse: RatepayerStatusResponse = StatusService.buildRatepayerStatusResponse(ratepayerStatus)

    Future.successful(Ok(Json.toJson(ratepayerStatusResponse)))
  }
}
