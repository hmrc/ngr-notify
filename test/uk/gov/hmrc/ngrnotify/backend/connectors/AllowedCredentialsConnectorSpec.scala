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

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.ngrnotify.connectors.AllowedCredentialsConnector

class AllowedCredentialsConnectorSpec extends AnyWordSpec with Matchers with ScalaFutures {

  private val connector                     = new AllowedCredentialsConnector()

  "AllowedCredentialsConnector" should {

    "return true for credentials from the list" in
      Seq("test-cred-1", "test-cred-2", "test-cred-3", "test-cred-4").foreach { credId =>
        connector.isAllowed(credId).futureValue shouldBe true
      }

    "return false for credentials not from the list" in {
      val notAllowedID: String = "test-cred-5"
      connector.isAllowed(notAllowedID).futureValue shouldBe false
    }
  }
}
