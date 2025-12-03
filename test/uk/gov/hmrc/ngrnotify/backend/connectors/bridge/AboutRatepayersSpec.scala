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

package uk.gov.hmrc.ngrnotify.backend.connectors.bridge

import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordControllerSpec
import uk.gov.hmrc.ngrnotify.connectors.bridge.AboutRatepayers
import uk.gov.hmrc.ngrnotify.model.bridge.JobMessage
import uk.gov.hmrc.ngrnotify.model.email.Email
import uk.gov.hmrc.ngrnotify.model.ratepayer.AgentStatus.agent
import uk.gov.hmrc.ngrnotify.model.ratepayer.RatepayerType.organization
import uk.gov.hmrc.ngrnotify.model.ratepayer.*
import uk.gov.hmrc.ngrnotify.model.{Address, Postcode}

class AboutRatepayersSpec extends AnyWordControllerSpec:

  override def fakeApplication(): Application = {
    val httpClient = mock[HttpClientV2] // No need to instruct this mock
    new GuiceApplicationBuilder()
      .overrides(bind[HttpClientV2].to(httpClient))
      .build()
  }

  "the AboutRatepayers connector helps" should {
    "process the template correctly" in {
      // SETUP

      // Load the template that the Bridge API service would provide over the network
      // and which we saved in a JSON file in the test resources directory (for convenience)
      val text           = testResourceContent("bridge/ratepayer-found.json")
      val bridgeTemplate = Json.parse(text).as[JobMessage]

      // Make up a realistic NGR request for having a ratepayer registered
      val ngrRequest = RegisterRatepayerRequest(
        ratepayerCredId = "whatever",
        userType = Some(organization),
        agentStatus = Some(agent),
        name = Some(Name("David Smith")),
        tradingName = None,
        email = Some(Email("david.smith@some.com")),
        nino = Some(Nino("QQ123456A")),
        contactNumber = Some(PhoneNumber("1111")),
        secondaryNumber = None,
        address = Some(Address("Line 1", Some("Line 2"), "City", None, Postcode("ZZ11 1ZZ"))),
        trnReferenceNumber = Some(TRNReferenceNumber(ReferenceType.TRN, "TRN123456")),
        isRegistered = Some(false),
        recoveryId = Some("AAH4-KKSW-7LX9")
      )

      // EXERCISE
      // by applying the process method with both the Bridge template and the NGR request
      val aboutRatepayers = inject[AboutRatepayers]
      val filled          = aboutRatepayers.process(bridgeTemplate, ngrRequest)

      // VERIFY
      // that the filled template actually got the NGR request data in the right spots
      whenReady(filled.toFuture) { filled =>
        filled.job.name.value.value                          shouldBe "Register David Smith"
        filled.job.compartments.products(0).name.value.value shouldBe "David Smith"
        // TODO filled.job.compartments.products(0).data.foreignIds should contain theSameElementsAs ???
        // TODO add more assertions here ...
      }
    }
  }
