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

package uk.gov.hmrc.ngrnotify.model.bridge

import play.api.libs.json.Json

class BridgeMessageSpec extends BridgeMessageBaseSpec:

  "the JSON deserialization mechanism" when {
    "dealing with ratepayers" should {
      "parse responses for the 'empty ratepayer' scenario" in {
        pending
        val jsonText = testResourceContent("responses/ratepayer-empty.json")
      }

      "parse responses for the 'existing ratepayer' scenario" in {
        val jsonText = testResourceContent("responses/ratepayer-existing.json")
        val jsValue = Json.parse(jsonText)
        val bridgeResponse = jsValue.as[BridgeResponse]
        bridgeResponse.job.idx shouldBe "1"
        // TODO make more assertions here
      }

      "parse responses for the 'invalid ratepayer' scenario" in {
        pending
        val jsonText = testResourceContent("responses/ratepayer-invalid.json")
        val jsValue = Json.parse(jsonText)
        val bridgeResponse = jsValue.as[BridgeResponse]
        bridgeResponse.job.idx shouldBe "1"
      }
    }

    "dealing with property links" should {
      "parse responses for the 'existing property link' scenario" in {
        val jsonText = testResourceContent("../ratepayerHasPropertyLink.json")
        val ratepayerResponseJson = Json.parse(jsonText)
        val bridgeResponse = ratepayerResponseJson.as[BridgeResponse]

        Json.toJson(bridgeResponse).as[BridgeResponse] shouldBe bridgeResponse
      }
    }
  }
