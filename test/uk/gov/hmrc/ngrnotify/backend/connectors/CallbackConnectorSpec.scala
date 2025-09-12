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

import java.net.URL
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.Status.OK
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.{RequestBuilderStub, TestData}
import uk.gov.hmrc.ngrnotify.connectors.CallbackConnector
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*

class CallbackConnectorSpec extends AnyWordAppSpec with TestData {

  private def httpPostMock(responseStatus: Int): HttpClientV2 = {
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.post(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus), "{}"))
    httpClientV2Mock
  }

  "callbackOnFailure" must {
    "not make HTTP call when no callback URL" in {
      val httpMock  = httpPostMock(OK)
      val connector = CallbackConnector(httpMock)

      val emailNotification = prefilledEmailRegistrationSuccessful.copy(callbackUrl = None)

      connector.callbackOnFailure(emailNotification, new Exception("error")).futureValue

      verify(httpMock, never)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "make HTTP call to callback URL when callback URL exists" in {
      val httpMock  = httpPostMock(OK)
      val connector = CallbackConnector(httpMock)

      val emailNotification = prefilledEmailRegistrationSuccessful.copy(callbackUrl = Some("https://test.com/callback"))

      connector.callbackOnFailure(emailNotification, new Exception("error")).futureValue

      verify(httpMock)
        .post(eqTo(url"https://test.com/callback"))(using any[HeaderCarrier])
    }

    "make HTTP call with explicit parameters" in {
      val httpMock  = httpPostMock(OK)
      val connector = CallbackConnector(httpMock)

      val emailNotification = prefilledEmailRegistrationSuccessful.copy(callbackUrl = Some("https://test.com/callback"))

      connector.callbackOnFailure(emailNotification, 400, ACTION_FAILED, "test").futureValue

      verify(httpMock)
        .post(eqTo(url"https://test.com/callback"))(using any[HeaderCarrier])
    }
  }
}
