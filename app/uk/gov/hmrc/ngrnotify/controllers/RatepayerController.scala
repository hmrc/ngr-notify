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
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RegisterRatepayerRequest, RegisterRatepayerResponse, RegistrationStatus}
import uk.gov.hmrc.ngrnotify.model.response.ApiFailure
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.collection.Seq
import scala.collection.immutable.ArraySeq
import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
@Singleton
class RatepayerController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
  with Logging:

  def registerRatepayer: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val result = request.body.validate[RegisterRatepayerRequest] match {
      case JsSuccess(registerRatepayerRequest, _)                   =>
        logger.info(s"Request:\n$registerRatepayerRequest")

        Created(Json.toJsObject(RegisterRatepayerResponse(RegistrationStatus.OK)))
      case JsError(errors: Seq[(JsPath, Seq[JsonValidationError])]) =>
        buildValidationErrorsResponse(errors)
    }

    Future.successful(result)
  }

  private def buildValidationErrorsResponse(
    errors: Seq[(JsPath, Seq[JsonValidationError])]
  ): Result =
    val failures = errors.map { case (jsPath, jsonErrors) =>
      ApiFailure(
        JSON_VALIDATION_ERROR,
        s"$jsPath <- ${jsonErrors.map(printValidationError).mkString(" | ")}"
      )
    }
    BadRequest(Json.toJson(failures))

  private def printValidationError(error: JsonValidationError): String =
    val msgArgs = error.args match {
      case arraySeq: ArraySeq[?] => arraySeq.mkString(", ")
      case any                   => any.toString
    }
    error.message + msgArgs
