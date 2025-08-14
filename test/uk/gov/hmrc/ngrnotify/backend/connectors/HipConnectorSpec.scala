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
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.HipTestData.testHipHeaders
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class HipConnectorSpec extends AnyWordAppSpec {

  private val configuration  = Configuration(ConfigFactory.load("application.conf"))

  private def httpGetMock(responseStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.get(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus)))
    httpClientV2Mock

  "callHelloWorld()" must {
    "return a successful JsValue response" in {
      val httpMock  = httpGetMock(OK)
      val connector = new HipConnector(httpMock)

      val response = connector.callHelloWorld(testHipHeaders).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .get(eqTo(url"https://hip.ws.ibt.hmrc.gov.uk/demo/hello-world"))(using any[HeaderCarrier])
    }
  }

  "callItems()" must {
    "return a successful JsValue response" in {
      val httpMock  = httpGetMock(OK)
      val connector = new HipConnector(httpMock)

      val response = connector.callItems(testHipHeaders).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .get(eqTo(url"https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/item"))(using any[HeaderCarrier])
    }
  }
}
