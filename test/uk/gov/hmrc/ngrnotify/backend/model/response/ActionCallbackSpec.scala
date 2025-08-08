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
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.ErrorCode.ACTION_FAILED
import uk.gov.hmrc.ngrnotify.model.response.{ActionCallback, ApiFailure}

import java.util.UUID

class ActionCallbackSpec extends AnyFlatSpec with Matchers {

  val id = "00000000-0000-0000-0000-000000000000"

  val ActionCallbackRequest: ActionCallback =
    ActionCallback(
      UUID.fromString(id),
      "test action",
      INTERNAL_SERVER_ERROR,
      Seq(ApiFailure(ACTION_FAILED, "failure reason"))
    )

  val ActionCallbackJson: JsValue = Json.parse(
    """
      |{
      |"trackerId": "00000000-0000-0000-0000-000000000000",
      |"action": "test action",
      |"status": 500,
      |"failures":[{"code":"ACTION_FAILED","reason":"failure reason"}]
      |}
      |""".stripMargin
  )

  "ApiFailure deserialize" should
    "deserialize to json" in {
      Json.toJson(ActionCallbackRequest) mustBe ActionCallbackJson
    }

  "ApiFailure serialize" should
    "serialize to json" in {
      ActionCallbackJson.as[ActionCallback] mustBe ActionCallbackRequest
    }

}
