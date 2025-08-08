package uk.gov.hmrc.ngrnotify.backend.services

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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.mvc.Headers
import uk.gov.hmrc.ngrnotify.backend.testUtils.HipTestData.{testClientId, testClientSecret, testHipHeaders}
import uk.gov.hmrc.ngrnotify.utils.AuthHeaderBuilder
import uk.gov.hmrc.ngrnotify.services.HipService

class HipServiceSpec extends AnyWordSpec with Matchers {
  private val clientIdOnlyHeaders: Headers = new Headers(Seq("Client-Id" -> testClientId))
  private val clientSecretOnlyHeaders: Headers = new Headers(Seq("Client-Secret" -> testClientSecret))
  private val emptyHeaders: Headers = new Headers(Seq())

  "extractClientId()" should {
    "return the client ID when present" in {
      HipService.extractClientId(clientIdOnlyHeaders) shouldBe "clientId"
    }

    "throw RuntimeException when client ID is missing" in {
      val ex = intercept[RuntimeException] {
        HipService.extractClientId(emptyHeaders)
      }
      ex.getMessage shouldBe "extractClientId ERROR: Client Id missing from headers"
    }
  }

  "extractClientSecret()" should {
    "return the client secret when present" in {
      HipService.extractClientSecret(clientSecretOnlyHeaders) shouldBe "clientSecret"
    }

    "throw RuntimeException when client secret is missing" in {
      val ex = intercept[RuntimeException] {
        HipService.extractClientSecret(emptyHeaders)
      }
      ex.getMessage shouldBe "extractClientSecret ERROR: Client Secret missing from headers"
    }
  }

  "buildAuthHeader()" should {
    "return the auth header using AuthHeaderBuilder" in {
      val expected = AuthHeaderBuilder.buildAuthHeader(testClientId, testClientSecret)

      HipService.buildAuthHeader(testHipHeaders) shouldBe expected
    }
  }

  "buildHipHeaderCarrier()" should {
    "build a HeaderCarrier with default headers" in {
      val carrier = HipService.buildHipHeaderCarrier(testHipHeaders)

      val expectedAuth = AuthHeaderBuilder.buildAuthHeader(testClientId, testClientSecret)

      carrier.extraHeaders should contain allOf (
        "Authorization" -> expectedAuth,
        "Accept" -> "application/json",
        "Content-Type" -> "application/json",
        "OriginatorId" -> "NGR"
      )
    }

    "include additional headers when provided" in {
      val additional = Some("Extra-Header" -> "Extra-Value")
      val carrier = HipService.buildHipHeaderCarrier(testHipHeaders, additional)

      carrier.extraHeaders should contain("Extra-Header" -> "Extra-Value")
    }
  }
}
