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

package uk.gov.hmrc.ngrnotify.backend.base

import org.mockito.ArgumentMatcher
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.ngrnotify.backend.testUtils.RequestBuilderStub

import java.net.URL
import scala.io.Source

abstract class AnyWordControllerSpec extends AnyWordSpec with GuiceOneAppPerSuite with AppSuiteBase:

  def testResourceContent(resource: String): String =
    Source.fromResource(resource).mkString

  def urlEndsWith(suffix: String) = new ArgumentMatcher[URL]:
    override def matches(url: URL): Boolean =
      url match
        case url: URL => url.toString.endsWith(suffix)
        case _        => false

  extension (httpClient: HttpClientV2)

    def whenGetting(suffix: String): OngoingStubbing[RequestBuilder] =
      when(
        httpClient.get(argThat(urlEndsWith(suffix)))(using any[HeaderCarrier])
      )

    def whenPosting(suffix: String): OngoingStubbing[RequestBuilder] =
      when(
        httpClient.post(argThat(urlEndsWith(suffix)))(using any[HeaderCarrier])
      )

  def rightResponseWith(status: Int, bodyResource: Option[String] = None): RequestBuilder =
    if bodyResource.isEmpty then RequestBuilderStub(Right(status))
    else RequestBuilderStub(Right(status), testResourceContent(bodyResource.get))

  def leftResponseWith(ex: Exception): RequestBuilder =
    RequestBuilderStub(Left(ex))
