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

import play.api.mvc.Headers
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.ngrnotify.services.HipService.buildHipHeaderCarrier

import java.net.URL

@Singleton
class HipConnector @Inject() (httpClient: HttpClientV2)(implicit ec: ExecutionContext) {

  def callHelloWorld(headers: Headers): Future[HttpResponse] = {
    val url: URL = url"https://hip.ws.ibt.hmrc.gov.uk/demo/hello-world"
    httpClient
      .get(url)(using buildHipHeaderCarrier(headers))
      .execute[HttpResponse]
  }

  def callPersonDetails(headers: Headers): Future[HttpResponse] = {
    val url: URL                                   = url"https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/persondetails"
    val additionalHeader: Option[(String, String)] = Some("CorrelationId" -> "f0bd1f32-de51-45cc-9b18-0520d6e3ab1a")
    httpClient
      .get(url)(using buildHipHeaderCarrier(headers, additionalHeader))
      .execute[HttpResponse]
  }

  def callItems(headers: Headers): Future[HttpResponse] = {
    val url: URL                                   = url"https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/item"
    val additionalHeader: Option[(String, String)] = Some("ItemNumber" -> "0")
    httpClient
      .get(url)(using buildHipHeaderCarrier(headers, additionalHeader))
      .execute[HttpResponse]
  }
}
