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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*

class JobMessageSpec extends AnyWordSpec with Matchers {

  "the JobMessage model" should {
    "be made from deserializing the ratepayer-found.json example" in {
      val text   = testResourceContent("bridge/ratepayer-found.json")
      val json   = Json.parse(text)
      val result = json.validate[JobMessage]
      result.isSuccess shouldBe true
    }

    "be made from deserializing the ratepayer-not-found.json example" in {
      // TODO - Fix this test case as soon as the model.bridge.ProductEntity.items[] get correctly mapped
      val text   = testResourceContent("bridge/ratepayer-not-found.json")
      val json   = Json.parse(text)
      val result = json.validate[JobMessage]
      result.isSuccess shouldBe true
    }

    "be serialized back to JSON matching the original get-properties-response example" in {
      val text         = testResourceContent("get-properties-response.json")
      val json         = Json.parse(text)
      val jobMessage   = json.as[JobMessage]
      val serialized   = Json.toJson(jobMessage)
      serialized      shouldBe json
    }
  }

}
