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

package uk.gov.hmrc.ngrnotify.backend.model

import play.api.libs.json.JsResultException
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.RatepayerStatus

class RatepayerStatusSpec extends AnyFlatSpec with Matchers {
  "RatepayerStatus" should "serialize to JSON" in {
      val status: RatepayerStatus = RatepayerStatus.ACCEPTED
      val json: JsValue = Json.toJson(status)

      json.toString() shouldBe "\"ACCEPTED\""
    }

  it should "deserialize from JSON" in {
    val json: JsValue = Json.parse("\"REJECTED\"")
    val status = json.as[RatepayerStatus]

    status shouldBe RatepayerStatus.REJECTED
  }

  it should "fail to deserialize invalid JSON" in {
    val json: JsValue = Json.parse("\"INVALID_STATUS\"")

    assertThrows[JsResultException] {
      json.as[RatepayerStatus]
    }
  }
}
