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

package uk.gov.hmrc.ngrnotify.backend.model.response

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus
import uk.gov.hmrc.ngrnotify.model.response.RatepayerStatusResponse

class RatepayerStatusResponseSpec extends AnyFlatSpec with Matchers {

  val ratepayerStatusResponse: RatepayerStatusResponse =
    RatepayerStatusResponse(RatepayerStatus.UNKNOWN, Some("random string"))

  val ratepayerStatusResponseJson: JsValue = Json.parse(
    """
      |{
      |  "ratepayerStatus": "UNKNOWN",
      |  "error": "random string"
      |}
      |""".stripMargin
  )

  "RatepayerStatusResponse" should "serialize to JSON" in {
    Json.toJson(ratepayerStatusResponse) mustBe ratepayerStatusResponseJson
  }

  it should "deserialize from JSON" in {
    ratepayerStatusResponseJson.as[RatepayerStatusResponse] mustBe ratepayerStatusResponse
  }
}
