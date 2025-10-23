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
import play.api.libs.json.{Format, JsError, Json}
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.{ngr_registration_successful, ngr_add_property_request_sent}
import uk.gov.hmrc.ngrnotify.model.{EmailTemplate, Scala3EnumJsonFormat}

/**
  * @author Yuriy Tumakha
  */
class EmailTemplateEnumSpec extends AnyFlatSpec with should.Matchers {

  "Scala3EnumFormat.format" should "serialize Scala 3 enum to json" in {
    val obj  = Seq(ngr_registration_successful, ngr_add_property_request_sent)
    val json = Json.toJson(obj)
    json.as[Seq[EmailTemplate]]  shouldBe obj
    Json.stringify(json) shouldBe """["ngr_registration_successful","ngr_add_property_request_sent"]"""
  }

  it should "deserialize Scala 3 enum from json" in {
    val obj = Json.parse("\"ngr_registration_successful\"").as[EmailTemplate]
    obj shouldBe ngr_registration_successful
  }

  it should "return JsError for wrong enum value" in {
    Json.parse("\"Cyan\"").validate[EmailTemplate] shouldBe JsError(
      "Enum value 'Cyan' is not in allowed list - ngr_registration_successful, ngr_add_property_request_sent"
    )
  }

  it should "return JsError for number" in {
    Json.parse("123").validate[EmailTemplate] shouldBe JsError("Invalid Json: expected string, got: 123")
  }

}
