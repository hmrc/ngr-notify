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

package uk.gov.hmrc.ngrnotify.backend.connectors

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.http.Status.{ACCEPTED, OK}
import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordControllerSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.RequestBuilderStub
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeRequest
import uk.gov.hmrc.ngrnotify.model.propertyDetails.CredId
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

@deprecated(
  message = "The HipConnector is going to be displaced by the new BridgeConnector",
  since = "2025-11-21"
)
class HipConnectorSpec extends AnyWordControllerSpec {

  private val configuration = Configuration(ConfigFactory.load())
  private val appConfig     = AppConfig(configuration, ServicesConfig(configuration))

  private def httpGetMock(responseStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.get(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus)))
    httpClientV2Mock

  private def httpPostMock(responseStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.post(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus), "{}"))
    httpClientV2Mock

  "getRatepayer" must {
    "return a successful response" in {
      val httpMock              = httpGetMock(OK)
      val connector             = HipConnector(appConfig, httpMock)
      given Request[AnyContent] = FakeRequest()

      val response = connector.getRatepayer(CredId("ID_123")).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .get(argThat(urlEndsWith("/ratepayers/ID_123")))(using any[HeaderCarrier])
    }
  }
}
