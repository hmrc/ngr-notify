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

import org.mongodb.scala.bson.ObjectId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.AllowedCredentials

class AllowedCredentialsSpec extends AnyFlatSpec with Matchers {

  private val objectId = "000012340000123400001234"

  val allowedCredentials: AllowedCredentials = AllowedCredentials(
    _id = new ObjectId(objectId),
    credId = "test-cred-id"
  )

  val allowedCredentialsJson: JsValue = Json.parse(
    s"""
       |{
       |  "id": {"$$oid":"$objectId"},
       |  "cred_id": "test-cred-id"
       |}
       |""".stripMargin
  )

  "AllowedCredentials" should "serialize to JSON correctly" in {
    Json.toJson(allowedCredentials) mustBe allowedCredentialsJson
  }

  it should "deserialize from JSON correctly" in {
    allowedCredentialsJson.as[AllowedCredentials] mustBe allowedCredentials
  }
}
