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

package uk.gov.hmrc.ngrnotify.backend.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.HipTestData.{testClientId, testClientSecret}
import uk.gov.hmrc.ngrnotify.utils.AuthHeaderBuilder

class AuthHeaderBuilderSpec extends AnyWordSpec with Matchers {

  "buildAuthHeader()" should {

    "return a valid Basic Auth header for given clientId and clientSecret" in {
      val testClientIdX = "659e20b1-da89-41c3-8074-dc640d7deca8"
      val testClientSecretX = "ZMIvNPFcHowPEzTUZC3GBhTCwqr189Cs"
      val expectedEncoded =
        java.util.Base64.getEncoder.encodeToString(s"$testClientIdX:$testClientSecretX".getBytes("UTF-8"))
//      val expectedEncoded =
//        java.util.Base64.getEncoder.encodeToString(s"$testClientId:$testClientSecret".getBytes("UTF-8"))
      val expectedHeader  = s"Basic $expectedEncoded"
      println(" XXX" + expectedHeader)

      val result = AuthHeaderBuilder.buildAuthHeader(testClientId, testClientSecret)

      result shouldBe expectedHeader
    }

    "handle empty clientId and clientSecret" in {
      val expectedEncoded = java.util.Base64.getEncoder.encodeToString(":".getBytes("UTF-8"))
      val expectedHeader  = s"Basic $expectedEncoded"

      val result = AuthHeaderBuilder.buildAuthHeader("", "")

      result shouldBe expectedHeader
    }

    "handle special characters in clientId and clientSecret" in {
      val clientId        = "id:with:special@chars"
      val clientSecret    = "secret/with?chars"
      val expectedEncoded = java.util.Base64.getEncoder.encodeToString(s"$clientId:$clientSecret".getBytes("UTF-8"))
      val expectedHeader  = s"Basic $expectedEncoded"

      val result = AuthHeaderBuilder.buildAuthHeader(clientId, clientSecret)

      result shouldBe expectedHeader
    }

    "produce different headers for different inputs" in {
      val header1 = AuthHeaderBuilder.buildAuthHeader("client1", "secret1")
      val header2 = AuthHeaderBuilder.buildAuthHeader("client2", "secret2")

      header1 should not be header2
    }
  }
}
