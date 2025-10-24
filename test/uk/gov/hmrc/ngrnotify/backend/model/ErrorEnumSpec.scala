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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsError, Json}
import uk.gov.hmrc.ngrnotify.model.ErrorCode
import uk.gov.hmrc.ngrnotify.model.ErrorCode.BAD_REQUEST_BODY

/**
  * @author Yuriy Tumakha
  */
class ErrorEnumSpec extends AnyFlatSpec with should.Matchers {

  "Scala3EnumFormat.format" should "serialize Scala 3 enum to json" in {
    val obj  = Seq(BAD_REQUEST_BODY)
    val json = Json.toJson(obj)
    json.as[Seq[ErrorCode]]  shouldBe obj
    Json.stringify(json) shouldBe """["BAD_REQUEST_BODY"]"""
  }

  it should "deserialize Scala 3 enum from json" in {
    val obj = Json.parse("\"BAD_REQUEST_BODY\"").as[ErrorCode]
    obj shouldBe BAD_REQUEST_BODY
  }

  it should "return JsError for wrong enum value" in {
    Json.parse("\"Cyan\"").validate[ErrorCode] shouldBe JsError(
      "Enum value 'Cyan' is not in allowed list - BAD_REQUEST_BODY, EMAIL_TEMPLATE_NOT_FOUND, MONGO_DB_ERROR, INVALID_EMAIL, WRONG_RESPONSE_STATUS, JSON_VALIDATION_ERROR, ACTION_FAILED, WRONG_RESPONSE_BODY"
    )
  }

  it should "return JsError for number" in {
    Json.parse("123").validate[ErrorCode] shouldBe JsError("Invalid Json: expected string, got: 123")
  }

}
