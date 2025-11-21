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

import play.api.libs.json.{JsError, JsValue, Json, JsonValidationError, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.{Accepted, BadRequest, InternalServerError, Ok}
import uk.gov.hmrc.ngrnotify.connectors.bridge.BridgeResult
import uk.gov.hmrc.ngrnotify.model.{ErrorCode, NgrNotifyMessage}
import uk.gov.hmrc.ngrnotify.model.ErrorCode.{ACTION_FAILED, JSON_VALIDATION_ERROR}
import uk.gov.hmrc.ngrnotify.model.response.ApiFailure

import scala.collection.immutable.ArraySeq
import scala.concurrent.{ExecutionContext, Future}

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

  /**
    * It extends the BridgeResult with the ability to convert it to a proper HTTP response
    * and also provides a common failure response builder.
    */
  extension [T](bridgeResult: BridgeResult[T])

    /**
      * Transforms a `BridgeResult` to an `HttpResult` by handling the encapsulated future.
      *
      * @param e The implicit `ExecutionContext` used to handle asynchronous operations.
      * @return A `Future[Result]` which represents the HTTP response.
      *         It can be:
      *         - `InternalServerError` if there is a failure (either `BridgeFailure` or an exception).
      *         - `Accepted` if the operation succeeds with no response body in the case of a successful result.
      */
    def toHttpResult()(using e: ExecutionContext): Future[Result] =
      bridgeResult
        // Handles the wrapped Scala Future of Either BridgeFailure or T
        .wrapped
        .map {
          // This happens when the Scala Future completes successfully with a value of type Either BridgeFailure or T
          // A left value indicates a failure message from the Bridge API, while a right value indicates a successful result
          case Left(bridgeFailure) =>
            InternalServerError(buildFailureResponse(ACTION_FAILED, bridgeFailure))
          case Right(_)            =>
            Accepted

          // Note that we're replying with 202 Accepted in case of positive response from Bridge API
          // and that there will be no response body in this case.
        }
        .recover {
          // This happens when the Scala Future fails with an exception
          // (for example, network error, JSON parsing error, etc.)
          case ex =>
            InternalServerError(buildFailureResponse(ACTION_FAILED, ex.getMessage))
        }

    // TODO def toHttpResultWithContent()(using w: Writes[T], e: ExecutionContext): Future[Result] = ???
