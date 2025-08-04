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
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec

//import uk.gov.hmrc.ngrnotify.backend.schema.Address
//import uk.gov.hmrc.ngrnotify.backend.utils.DateUtilLocalised
import uk.gov.hmrc.ngrnotify.connector.EmailConnector
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class EmailConnectorSpec extends AnyWordAppSpec {

  private val configuration                = Configuration(ConfigFactory.load("application.conf"))
  private val servicesConfig               = new ServicesConfig(configuration)
  implicit val hc: HeaderCarrier           = HeaderCarrier()

  private def httpPostMock(responseStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.post(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus)))
    httpClientV2Mock

  "EmailConnector" must {
    "verify that the email service is called on send ngr-notify" in {
      val httpMock  = httpPostMock(OK)
      val connector = new EmailConnector(servicesConfig, httpMock)

      val response = connector.sendEmailNotification(prefilledEmail).futureValue
      response.status shouldBe OK

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

  }

}
