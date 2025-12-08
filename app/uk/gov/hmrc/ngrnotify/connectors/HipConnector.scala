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

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Request
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.connectors.bridge.HipHeaderCarrier
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeRequest
import uk.gov.hmrc.ngrnotify.model.propertyDetails.CredId

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@deprecated(
  message = "This connector is going to be displaced by the new BridgeConnector",
  since = "2025-11-21"
)
@Singleton
class HipConnector @Inject() (
  appConfig: AppConfig,
  httpClient: HttpClientV2
)(implicit ec: ExecutionContext
) extends HipHeaderCarrier(appConfig):

  @deprecated("This method is going to be moved to the new BridgeConnector", "2025-11-21")
  def updatePropertyChanges(bridgeRequest: BridgeRequest)(using request: Request[?]): Future[HttpResponse] =
    httpClient
      .post(appConfig.updatePropertyChangesUrl)(using hipHeaderCarrier)
      .withBody(Json.toJson(bridgeRequest))
      .execute[HttpResponse]

  @deprecated("This method is going to be moved to the new BridgeConnector", "2025-11-21")
  def submitPropertyLinkingChanges(bridgeRequest: BridgeRequest)(using request: Request[?]): Future[HttpResponse] = httpClient
    .post(appConfig.propertyLinkingUrl)(using hipHeaderCarrier)
    .withBody(Json.toJson(bridgeRequest))
    .execute[HttpResponse]

  @deprecated("This method is going to be moved to the new BridgeConnector", "2025-11-21")
  def getRatepayer(id: String)(using request: Request[?]): Future[HttpResponse] =
    httpClient
      .get(appConfig.getRatepayerUrl(id))(using hipHeaderCarrier)
      .execute[HttpResponse]

  @deprecated("This method is going to be moved to the new BridgeConnector", "2025-11-21")
  def getRatepayerStatus(id: CredId)(using request: Request[?]): Future[HttpResponse] =
    httpClient
      .get(appConfig.getRatepayerStatusUrl(id))(using hipHeaderCarrier)
      .execute[HttpResponse]
