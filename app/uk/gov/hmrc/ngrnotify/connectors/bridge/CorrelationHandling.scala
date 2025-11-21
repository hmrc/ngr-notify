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

package uk.gov.hmrc.ngrnotify.connectors.bridge

import play.api.http.HeaderNames.{ACCEPT, AUTHORIZATION, CONTENT_TYPE}
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.utils.AuthHeaderBuilder

import java.util.UUID


trait CorrelationHandling(appConfig: AppConfig):

  private val ORIGINATOR_ID = "OriginatorId"
  private val CORRELATION_ID = "CorrelationId"

  private val staticHeaders: Seq[(String, String)] = Seq(
    AUTHORIZATION -> AuthHeaderBuilder.buildAuthHeader(appConfig.hipClientId, appConfig.hipClientSecret),
    CONTENT_TYPE -> "application/json;charset=UTF-8",
    ACCEPT -> "application/json;charset=UTF-8",
    ORIGINATOR_ID -> "NGR"
  )

  private def newCorrelationId: (String, String) =
    CORRELATION_ID -> UUID.randomUUID.toString

  private def forwardOrCreateCorrelationId(using request: Request[?]): (String, String) =
    request.headers.headers.find(_._1.equalsIgnoreCase(CORRELATION_ID)).getOrElse(newCorrelationId)

  def hipHeaderCarrier(using request: Request[?]): HeaderCarrier =
    HeaderCarrier(extraHeaders = staticHeaders :+ forwardOrCreateCorrelationId)

