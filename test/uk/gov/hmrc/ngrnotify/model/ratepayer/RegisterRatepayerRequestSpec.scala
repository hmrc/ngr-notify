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

package uk.gov.hmrc.ngrnotify.model.ratepayer

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.{Address, Postcode}
import uk.gov.hmrc.ngrnotify.model.email.Email
import uk.gov.hmrc.ngrnotify.model.ratepayer.AgentStatus.agent
import uk.gov.hmrc.ngrnotify.model.ratepayer.RatepayerType.organization

/**
  * @author Yuriy Tumakha
  */
class RegisterRatepayerRequestSpec extends AnyWordSpec with Matchers:

  "Model RegisterRatepayerRequest" should {
    "be serialized/deserialized from JSON" in {
      val registerRatepayerRequest = RegisterRatepayerRequest(
        "login",
        Some(organization),
        Some(agent),
        Some(Name("Full name")),
        Some(TradingName("Acme Ltd")),
        Some(Email("test@email.com")),
        Some(Nino("QQ123456A")),
        Some(PhoneNumber("1111")),
        None,
        Some(Address("Line 1", Some("Line 2"), "City", None, Postcode("ZZ11 1ZZ"))),
        Some(TRNReferenceNumber(ReferenceType.TRN, "TRN123456"))
      )

      val json = Json.toJson(registerRatepayerRequest)
      json.as[RegisterRatepayerRequest] shouldBe registerRatepayerRequest
    }
  }
