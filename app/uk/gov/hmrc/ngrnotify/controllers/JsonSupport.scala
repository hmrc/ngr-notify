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

import play.api.libs.json.{JsError, JsValue, Json, JsonValidationError}
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import uk.gov.hmrc.ngrnotify.model.ErrorCode
import uk.gov.hmrc.ngrnotify.model.ErrorCode.JSON_VALIDATION_ERROR
import uk.gov.hmrc.ngrnotify.model.response.ApiFailure

import scala.collection.immutable.ArraySeq

/**
  * @author Yuriy Tumakha
  */
trait JsonSupport:

  def buildFailureResponse(code: ErrorCode, reason: String): JsValue =
    Json.toJson(Seq(ApiFailure(code, reason)))

  def buildValidationErrorsResponse(jsError: JsError): Result =
    val failures = jsError.errors.map { case (jsPath, jsonErrors) =>
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
