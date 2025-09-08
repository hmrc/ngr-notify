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

package uk.gov.hmrc.ngrnotify.model.response

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.ngr_registration_successful
import uk.gov.hmrc.ngrnotify.model.ErrorCode.BAD_REQUEST_BODY

import java.util.UUID

/**
  * @author Yuriy Tumakha
  */
class ActionCallbackSpec extends AnyWordSpec with Matchers:

  "Model ActionCallback" should {
    "be serialized/deserialized from JSON" in {
      val actionCallback = ActionCallback(
        UUID.fromString("9d2dee33-7803-485a-a2b1-2c7538e597ee"),
        ngr_registration_successful.toString,
        BAD_REQUEST,
        Seq(ApiFailure(BAD_REQUEST_BODY, "Missed parameter - postcodeEndPart"))
      )

      val json = Json.toJson(actionCallback)
      json.as[ActionCallback] shouldBe actionCallback
    }
  }
