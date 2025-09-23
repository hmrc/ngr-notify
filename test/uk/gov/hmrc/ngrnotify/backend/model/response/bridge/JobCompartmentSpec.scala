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

package uk.gov.hmrc.ngrnotify.backend.model.response.bridge

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*
import uk.gov.hmrc.ngrnotify.backend.testUtils.BridgeModelTestData.testJobCompartmentEmpty
import uk.gov.hmrc.ngrnotify.model.response.bridge.JobCompartment

class JobCompartmentSpec extends AnyWordSpec with Matchers {
  private val jobCompartmentJson = Json.obj(
      "id" -> "ID",
      "idx" -> "IDX",
      "name" -> "TestJobName",
      "label" -> "TestJobLabel",
      "description" -> "TestJobDescription",
      "origination" -> "TestJobOrigination",
      "termination" -> "TestJobTermination",
      "category" -> Json.obj(
        "code" -> "TestCategoryCode",
        "meaning" -> "TestCategoryMeaning"
      ),
      "typeX" -> Json.obj(
        "code" -> "TestTypeXCode",
        "meaning" -> "TestTypeXMeaning"
      ),
      "classX" -> Json.obj(
        "code" -> "TestClassXCode",
        "meaning" -> "TestClassXMeaning"
      ),
      "data" -> Json.obj(
        "foreign_ids" -> Json.arr("1", "2", "3"),
        "foreign_names" -> Json.arr("Bob", "Brian","Bill"),
        "foreign_labels" -> Json.arr("Label1", "Label2", "Label3")
      ),
      "protodata" -> Json.arr("A", "B", "C"),
      "metadata" -> Json.obj(
        "sending" -> Json.obj(
          "extracting" -> Json.obj("selecting" -> Json.obj()),
          "transforming" -> Json.obj("recontextualising" -> Json.obj(), "filtering" -> Json.obj(), "supplementing" -> Json.obj()),
          "loading" -> Json.obj("assuring" -> Json.obj(), "readying" -> Json.obj(), "signing" -> Json.obj(), "encrypting" -> Json.obj(), "sending" -> Json.obj())
        ),
        "receiving" -> Json.obj(
          "transforming" -> Json.obj("recontextualising" -> Json.obj(), "dropping" -> Json.obj(), "restoring" -> Json.obj()),
          "storing" -> Json.obj("inserting" -> Json.obj()),
          "unloading" -> Json.obj("assuring" -> Json.obj(), "readying" -> Json.obj(), "verifying" -> Json.obj(), "decrypting" -> Json.obj(), "receiving" -> Json.obj())
        )
      ),
      "compartments" -> Json.obj("properties" -> Json.arr(), "persons" -> Json.arr(), "processes" -> Json.arr(), "relationships" -> Json.arr(), "products" -> Json.arr()),
      "items" -> Json.arr(Json.obj(), Json.obj(), Json.obj()))

  "JobCompartment" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(testJobCompartmentEmpty)
      json shouldBe jobCompartmentJson
    }

    "deserialize from JSON correctly" in {
      val json = jobCompartmentJson
      val job = json.as[JobCompartment]
      job shouldBe testJobCompartmentEmpty
    }

    "round-trip JSON serialization and deserialization" in {
      val original = jobCompartmentJson
      val json = Json.toJson(original)
      val parsed = json.as[JobCompartment]
      parsed shouldBe testJobCompartmentEmpty
    }
  }
}

