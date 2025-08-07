/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.ErrorCode.ACTION_FAILED
import uk.gov.hmrc.ngrnotify.model.email.{AddPropertyRequestSent, RegistrationSuccessful}
import uk.gov.hmrc.ngrnotify.model.response.{ApiFailure, ApiSuccess}

class ApiSuccessSpec extends AnyFlatSpec with Matchers {

  val ApiSuccessRequest: ApiSuccess = ApiSuccess("Success", "test message")

  val ApiFailureJson: JsValue = Json.parse(
    """
      |{
      |"status": "Success",
      |"message": "test message"
      |}
      |""".stripMargin
  )

  "ApiSuccess deserialize" should
    "deserialize to json" in {
      Json.toJson(ApiSuccessRequest) mustBe ApiFailureJson
    }

  "ApiSuccess serialize" should
    "serialize to json" in {
      ApiFailureJson.as[ApiSuccess] mustBe ApiSuccessRequest
    }

}
