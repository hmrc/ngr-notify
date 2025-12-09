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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class JobFormSpec extends AnyWordSpec with Matchers {

  "the JobForm model" should {
    "be made from deserializing the ratepayer-found.json example" in {
      val text   = testResourceContent("bridge/ratepayer-found.json")
      val json   = Json.parse(text)
      val result = json.validate[JobForm]
      result.isSuccess shouldBe true
    }

    "be made from deserializing the ratepayer-not-found.json example" in {
      // TODO - Fix this test case as soon as the model.bridge.ProductEntity.items[] get correctly mapped
      val text   = testResourceContent("bridge/ratepayer-not-found.json")
      val json   = Json.parse(text)
      val result = json.validate[JobForm]
      result.isSuccess shouldBe true
    }

    "be serialized back to JSON matching the original get-properties-response example" in {
      val text       = testResourceContent("complete_notification_information.json")
      val json       = Json.parse(text)
      val JobForm = json.as[JobForm]
      val serialized = Json.toJson(JobForm)

      serialized shouldBe json
    }
  }

}
