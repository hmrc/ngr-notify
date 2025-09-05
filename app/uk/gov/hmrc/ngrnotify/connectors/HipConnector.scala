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

package uk.gov.hmrc.ngrnotify.connectors

import play.api.http.HeaderNames.{ACCEPT, AUTHORIZATION, CONTENT_TYPE}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.{Headers, Request}
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeRequest
import uk.gov.hmrc.ngrnotify.services.HipService.buildHipHeaderCarrier
import uk.gov.hmrc.ngrnotify.utils.AuthHeaderBuilder

import java.net.URL
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HipConnector @Inject() (
  appConfig: AppConfig,
  httpClient: HttpClientV2
)(implicit ec: ExecutionContext
):

  private val ORIGINATOR_ID  = "OriginatorId"
  private val CORRELATION_ID = "CorrelationId"

  private val staticHeaders: Seq[(String, String)] = Seq(
    AUTHORIZATION -> AuthHeaderBuilder.buildAuthHeader(appConfig.hipClientId, appConfig.hipClientSecret),
    CONTENT_TYPE  -> "application/json;charset=UTF-8",
    ACCEPT        -> "application/json;charset=UTF-8",
    ORIGINATOR_ID -> "NGR"
  )

  private def newCorrelationId: (String, String) =
    CORRELATION_ID -> UUID.randomUUID.toString

  private def forwardOrCreateCorrelationId(using request: Request[?]): (String, String) =
    request.headers.headers.find(_._1.equalsIgnoreCase(CORRELATION_ID)).getOrElse(newCorrelationId)

  private def hipHeaderCarrier(using request: Request[?]): HeaderCarrier =
    HeaderCarrier(extraHeaders = staticHeaders :+ forwardOrCreateCorrelationId)

  def registerRatepayer(bridgeRequest: BridgeRequest)(using request: Request[?]): Future[HttpResponse] =
    httpClient
      .post(appConfig.registerRatepayerUrl)(using hipHeaderCarrier)
      .withBody(Json.toJson(bridgeRequest))
      .execute[HttpResponse]

  def callHelloWorld(headers: Headers): Future[HttpResponse] = {
    val url: URL = url"https://hip.ws.ibt.hmrc.gov.uk/demo/hello-world"
    httpClient
      .get(url)(using buildHipHeaderCarrier(headers))
      .execute[HttpResponse]
  }

  def callItems(headers: Headers): Future[HttpResponse] = {
    val url: URL                                   = url"https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/item"
    val additionalHeader: Option[(String, String)] = Some("ItemNumber" -> "0")
    httpClient
      .get(url)(using buildHipHeaderCarrier(headers, additionalHeader))
      .execute[HttpResponse]
  }
