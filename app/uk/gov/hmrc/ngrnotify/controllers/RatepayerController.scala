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

        Accepted(Json.toJsObject(RegisterRatepayerResponse(RegistrationStatus.OK)))
      case jsError: JsError                => buildValidationErrorsResponse(jsError)
    }

    Future.successful(result)
  }
