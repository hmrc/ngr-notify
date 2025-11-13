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
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.HipTestData.testHipHeaders
import uk.gov.hmrc.ngrnotify.backend.testUtils.RequestBuilderStub
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.connectors.HipConnector
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.MetadataStage
import uk.gov.hmrc.ngrnotify.model.bridge.{BridgeJobModel, BridgeRequest, Compartments, Job}
import uk.gov.hmrc.ngrnotify.model.propertyDetails.CredId
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class HipConnectorSpec extends AnyWordAppSpec {

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

  "registerRatepayer" must {
    "return a successful response" in {
      val httpMock              = httpPostMock(ACCEPTED)
      val connector             = HipConnector(appConfig, httpMock)
      given Request[AnyContent] = FakeRequest()
      val bridgeRequest         = BridgeRequest(
        Job(
          id = None,
          idx = "1",
          name = "Register Ratepayer",
          compartments = Compartments()
        )
      )

      val response = connector.registerRatepayer(bridgeRequest).futureValue
      response.status shouldBe ACCEPTED

      verify(httpMock)
        .post(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer"))(using any[HeaderCarrier])
    }
  }

  "updatePropertyChanges" must {
    "return a successful response" in {
      val httpMock              = httpPostMock(ACCEPTED)
      val connector             = HipConnector(appConfig, httpMock)
      given Request[AnyContent] = FakeRequest()

      val toBridgeJob = BridgeJobModel.Job(
        id = None,
        idx = Some("?"),
        name = Some("physical"),
        label = Some("Physical Job"),
        description = Some("Default physical job item"),
        origination = None,
        termination = None,
        category = BridgeJobModel.CodeMeaning(None, None),
        `type` = BridgeJobModel.CodeMeaning(None, None),
        `class` = BridgeJobModel.CodeMeaning(None, None),
        data = BridgeJobModel.Data(Nil, Nil, Nil),
        protodata = Seq.empty,
        metadata = BridgeJobModel.Metadata(MetadataStage(), MetadataStage()),
        compartments = BridgeJobModel.Compartments(),
        items = None
      )

      val bridgeRequest = BridgeJobModel(
        $schema = "http://example.com/schema",
        job = toBridgeJob
      )

      val response = connector.updatePropertyChanges(bridgeRequest).futureValue
      response.status shouldBe ACCEPTED

      verify(httpMock)
        .post(eqTo(url"http://localhost:1501/ngr-stub/hip/job/physical"))(using any[HeaderCarrier])
    }
  }

  "submitPropertyLinkingChanges" must {
    "return a successful response" in {
      val httpMock              = httpPostMock(ACCEPTED)
      val connector             = HipConnector(appConfig, httpMock)
      given Request[AnyContent] = FakeRequest()
      val bridgeRequest         = BridgeRequest(
        Job(
          id = None,
          idx = "1",
          name = "Property Linking",
          compartments = Compartments()
        )
      )

      val response = connector.submitPropertyLinkingChanges(bridgeRequest).futureValue
      response.status shouldBe ACCEPTED

      verify(httpMock)
        .post(eqTo(url"http://localhost:1501/ngr-stub/hip/job/property"))(using any[HeaderCarrier])
    }
  }

  "getRatepayer" must {
    "return a successful response" in {
      val httpMock              = httpGetMock(OK)
      val connector             = HipConnector(appConfig, httpMock)
      given Request[AnyContent] = FakeRequest()

      val response = connector.getRatepayer(CredId("ID_123")).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .get(eqTo(url"http://localhost:1501/ngr-stub/hip/job/ratepayer/ID_123"))(using any[HeaderCarrier])
    }
  }

  "callHelloWorld()" must {
    "return a successful JsValue response" in {
      val httpMock  = httpGetMock(OK)
      val connector = HipConnector(appConfig, httpMock)

      val response = connector.callHelloWorld(testHipHeaders).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .get(eqTo(url"https://hip.ws.ibt.hmrc.gov.uk/demo/hello-world"))(using any[HeaderCarrier])
    }
  }

  "callItems()" must {
    "return a successful JsValue response" in {
      val httpMock  = httpGetMock(OK)
      val connector = HipConnector(appConfig, httpMock)

      val response = connector.callItems(testHipHeaders).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .get(eqTo(url"https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/item"))(using any[HeaderCarrier])
    }
  }
}
